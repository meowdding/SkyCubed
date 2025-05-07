package tech.thatgravyboat.skycubed.features.tablist

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.toColumn
import me.owdding.lib.displays.toRow
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.Style
import tech.thatgravyboat.skyblockapi.api.area.hub.SpookyFestivalAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.effects.EffectsAPI
import tech.thatgravyboat.skyblockapi.api.profile.friends.FriendsAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.extentions.chunked
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.time.until
import tech.thatgravyboat.skycubed.api.ExtraDisplays
import tech.thatgravyboat.skycubed.config.overlays.TabListOverlay
import tech.thatgravyboat.skycubed.features.tablist.Line.Companion.EMPTY
import tech.thatgravyboat.skycubed.features.tablist.Line.Companion.toLine
import tech.thatgravyboat.skycubed.features.tablist.Line.Companion.toLines
import tech.thatgravyboat.skycubed.utils.ContributorHandler
import tech.thatgravyboat.skycubed.utils.formatReadableTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

private typealias Segment = List<Line>

private data class Line(
    val component: Component,
    var face: PlayerSkin? = null,
    var playerName: String? = null,
    var skyblockLevel: Int? = null,
    var contribEmblem: String? = null
) {
    val string: String get() = component.string
    val stripped: String get() = component.stripped

    companion object {
        fun List<Component>.toLines() = map { Line(it) }
        fun Component.toLine() = Line(this)
        val EMPTY = Line(CommonText.EMPTY)
    }
}

enum class CompactTablistSorting {
    NORMAL,
    SKYBLOCK_LEVEL,
    ALPHABETICAL,
    FRIENDS,
    ;

    private val formattedName = name.split("_").joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }

    override fun toString() = formattedName
}

object CompactTablist {

    private var display: Display? = null
    private var lastTablist: List<List<Component>> = emptyList()
    private val titleRegex = "\\s+Info".toRegex()
    private val playerRegex = "\\[(?<level>\\d+)] (?<name>[\\w_-]+).*".toRegex()
    private var boosterCookieInFooter = false
    private var godPotionInFooter = false
    private var filteredFooter: List<FormattedText> = emptyList()

    fun onSortingUpdate() {
        createDisplay(lastTablist)
    }

    fun onEnabledDisabled(enabled: Boolean) {
        if (enabled) {
            createDisplay(lastTablist)
        } else {
            display = null
        }
    }

    @Subscription
    fun onTablistUpdate(event: TabListChangeEvent) {
        lastTablist = event.new
        if (!isEnabled()) return
        createDisplay(lastTablist)
    }

    @Subscription
    fun onFooterUpdate(event: TabListHeaderFooterChangeEvent) {
        boosterCookieInFooter = event.newFooter.string.contains("\nCookie Buff\n")
        godPotionInFooter = event.newFooter.string.contains("\nYou have a God Potion active!")
        handleLeftOverFooterLines(event.newFooter)
        if (!isEnabled()) return

        createDisplay(lastTablist)
    }

    private fun createDisplay(tablist: List<List<Component>>) {
        val segments = tablist.flatMap { it + listOf(CommonText.EMPTY) }.toLines().addFooterSegment()
            .map { if (titleRegex.match(it.stripped)) EMPTY else it }
            .chunked { it.string.isBlank() }
            .map { it.filterNot { it.string.isBlank() } }.filterNot(List<Line>::isEmpty)
            .splitListIntoParts(4)
            .map { it.flatMap { it + listOf(EMPTY) } }
            .map { list -> list.map { it.formatPlayer() } }
            .map { it.sortPlayers() }

        val mainElement = segments.map { segment ->
            segment.map { line ->
                line.face?.let { face ->
                    listOfNotNull(
                        Displays.face({ face.texture }),
                        Displays.text(line.component),
                        line.contribEmblem?.let { Displays.text(it) },
                    ).toRow(3)
                } ?: Displays.text(line.component)
            }.toColumn()
        }.toRow(5)

        val footerElement =
            filteredFooter.map { Displays.center(mainElement.getWidth(), display = Displays.text(it)) }.toColumn()

        display = ExtraDisplays.background(
            0xA0000000u, 2f,
            Displays.padding(5, listOf(mainElement, footerElement).toColumn()),
        )
    }

    val footerLinesToRemove = listOf(
        "Cookie Buff",
        "God Potion",
        "Spooky Festival",
        "STORE.HYPIXEL.NET",
        "Ranks, Boosters & MORE!",
        "Not active! Obtain booster cookies",
        "shop in the hub.",
        "Use \"/effects\" to see the effects!",
        "Use \"/effects\" to see them!",
        "Active Effects",
    )

    private fun handleLeftOverFooterLines(footer: Component) {
        val split = McFont.self.splitter.splitLines(footer, Int.MAX_VALUE, Style.EMPTY)

        val filteredFooter = split.filter { it.string.stripColor().isNotBlank() }

        this.filteredFooter = filteredFooter.filter { line ->
            footerLinesToRemove.none { line.string.contains(it) }
        }
    }

    private fun Line.formatPlayer(): Line {
        playerRegex.match(this.string, "level", "name") { (level, name) ->
            val player = McClient.players.firstOrNull { it.profile.name == name }
            val contributor = ContributorHandler.contributors[player?.profile?.id]

            playerName = name
            face = player?.skin
            skyblockLevel = level.toInt()
            contributor?.symbol?.let { contribEmblem = it }
        }
        return this
    }

    private fun List<Line>.sortPlayers(): List<Line> {
        val linesWithLevels = this.filter { it.skyblockLevel != null }.sortedWith { o1, o2 ->
            when (TabListOverlay.sorting) {
                CompactTablistSorting.SKYBLOCK_LEVEL -> o2.skyblockLevel?.compareTo(o1.skyblockLevel ?: 0) ?: 0
                CompactTablistSorting.ALPHABETICAL -> o1.playerName?.compareTo(o2?.playerName ?: "", true) ?: 0
                CompactTablistSorting.FRIENDS -> {
                    val o1Friend = o1?.playerName?.let(FriendsAPI::getFriend)
                    val o2Friend = o2?.playerName?.let(FriendsAPI::getFriend)
                    return@sortedWith when {
                        o1Friend == null && o2Friend == null -> 0 // Not Friends
                        o1Friend == null -> -1 // o2 is friends but o1 is not
                        o2Friend == null -> 1 // o1 is friends but o2 is not
                        o1Friend.bestFriend && o2Friend.bestFriend -> 0 // o1 and o2 are both best friends
                        o1Friend.bestFriend -> 1 // o1 is best friends but o2 is not
                        o2Friend.bestFriend -> -1 // o2 is best friends but o1 is not
                        else -> 0 // o1 and o2 are both friends but not best friends
                    }
                }

                else -> 0
            }
        }

        val iterator = linesWithLevels.iterator()

        return this.map { line ->
            if (line.skyblockLevel != null) iterator.next() else line
        }
    }

    // TODO: Potion effects
    private fun Segment.addFooterSegment(): Segment = this + buildList {
        fun createDuration(
            label: String,
            duration: Duration,
            activeColor: Int,
            inactiveText: String = ": Inactive"
        ) = Text.join(
            Text.of(label) {
                this.color = activeColor
                this.bold = true
            },
            Text.of(
                if (duration.inWholeSeconds > 0) ": ${duration.formatReadableTime(DurationUnit.DAYS, 2)}"
                else inactiveText
            ).withColor(TextColor.GRAY)
        ).toLine()

        if (boosterCookieInFooter) {
            add(createDuration(" Cookie Buff", EffectsAPI.boosterCookieExpireTime.until(), TextColor.LIGHT_PURPLE))
        }
        if (godPotionInFooter) {
            add(createDuration(" God Potion", EffectsAPI.godPotionDuration, TextColor.RED))
        }
        if (SpookyFestivalAPI.onGoing) {
            add(
                Text.join(
                    Text.of(" Spooky Festival") {
                        this.color = TextColor.GOLD
                        this.bold = true
                    },
                    Text.of(": ").withColor(TextColor.GRAY),
                    Text.of(SpookyFestivalAPI.greenCandy.toString()).withColor(TextColor.GREEN),
                    Text.of(", ").withColor(TextColor.GRAY),
                    Text.of(SpookyFestivalAPI.purpleCandy.toString()).withColor(TextColor.DARK_PURPLE),
                    Text.of(" (").withColor(TextColor.GRAY),
                    Text.of(SpookyFestivalAPI.points.toString()).withColor(TextColor.GOLD),
                    Text.of(")").withColor(TextColor.GRAY)
                ).toLine()
            )
        }

        if (size > 1) {
            add(0, Text.of("Other:") {
                this.color = TextColor.YELLOW
                this.bold = true
            }.toLine())
        }
    }

    fun renderCompactTablist(graphics: GuiGraphics): Boolean {
        if (!isEnabled()) return false
        val display = display ?: return false

        Displays.center(width = graphics.guiWidth(), display = display).render(graphics, 0, 3)

        return true
    }


    private fun List<Segment>.splitListIntoParts(numberOfParts: Int): List<List<Segment>> {
        val totalSize = this.sumOf { it.size }

        val baseSize = totalSize / numberOfParts
        val extraSize = totalSize % numberOfParts

        val result = mutableListOf<List<Segment>>()
        var currentPart = mutableListOf<Segment>()
        var currentSize = 0
        var currentPartSize = baseSize

        var extraParts = extraSize

        for (segment in this) {
            currentPart.add(segment)
            currentSize += segment.size

            if (currentSize >= currentPartSize) {
                result.add(currentPart)
                currentPart = mutableListOf()
                currentSize = 0

                if (extraParts > 0) {
                    currentPartSize = baseSize + 1
                    extraParts--
                } else {
                    currentPartSize = baseSize
                }
            }
        }

        if (currentPart.isNotEmpty()) {
            result.add(currentPart)
        }

        return result
    }

    private fun isEnabled() = LocationAPI.isOnSkyBlock && TabListOverlay.enabled
}
