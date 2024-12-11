package tech.thatgravyboat.skycubed.features.tablist

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.hub.SpookyFestivalAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.effects.EffectsAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.api.displays.Display
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.api.displays.toRow
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.features.tablist.Line.Companion.EMPTY
import tech.thatgravyboat.skycubed.features.tablist.Line.Companion.toLine
import tech.thatgravyboat.skycubed.features.tablist.Line.Companion.toLines
import tech.thatgravyboat.skycubed.utils.formatReadableTime
import tech.thatgravyboat.skycubed.utils.until
import kotlin.time.Duration
import kotlin.time.DurationUnit

private typealias Segment = List<Line>

// todo: maybe emblems?
private data class Line(
    val component: Component,
    val face: PlayerSkin? = null,
    val skyblockLevel: Int? = null
) {
    val string: String get() = component.string
    val stripped: String get() = component.stripped

    companion object {
        fun List<Component>.toLines() = map { Line(it) }
        fun Component.toLine() = Line(this)
        val EMPTY = Line(CommonText.EMPTY)
    }
}

object CompactTablist {

    private var display: Display? = null
    private var lastTablist: List<List<Component>> = emptyList()
    private val titleRegex = "\\s+Info".toRegex()
    private val playerRegex = "\\[(?<level>\\d+)] (?<name>[\\w_-]+).*".toRegex()
    private var boosterCookieInFooter = false
    private var godPotionInFooter = false

    @Subscription
    fun onTablistUpdate(event: TabListChangeEvent) {
        createDisplay(event.new)
    }

    @Subscription
    fun onFooterUpdate(event: TabListHeaderFooterChangeEvent) {
        boosterCookieInFooter = event.newFooter.string.contains("\nCookie Buff\n")
        godPotionInFooter = event.newFooter.string.contains("\nYou have a God Potion active!")

        createDisplay(lastTablist)
    }

    private fun createDisplay(tablist: List<List<Component>>) {
        val segments = tablist.flatMap { it + listOf(CommonText.EMPTY) }.toLines().addFooterSegment()
            .map { if (titleRegex.match(it.stripped)) EMPTY else it }.chunked { it.string.isBlank() }
            .map { it.filterNot { it.string.isBlank() } }.filterNot(List<Line>::isEmpty)
            .splitListIntoParts(4)
            .map { it.flatMap { it + listOf(EMPTY) } }
            .map { list ->
                list.map { line ->
                    var skin: PlayerSkin? = null
                    var skyblockLevel: Int? = null
                    playerRegex.match(line.string, "level", "name") { (level, name) ->
                        skin = McClient.players.firstOrNull { it.profile.name == name }?.skin
                        skyblockLevel = level.toInt()
                    }
                    Line(line.component, skin, skyblockLevel)
                }
            }

        // TODO: Sort by skyblock level
        val columns = segments.map { segment ->
            Displays.column(
                *segment.map { (component, skin) ->
                    skin?.let {
                        Displays.row(
                            Displays.face({ it.texture }),
                            Displays.text(component),
                            spacing = 3
                        )
                    } ?: Displays.text(component)
                }.toTypedArray()
            )
        }

        val mainElement = columns.toRow(5)

        lastTablist = tablist

        display = Displays.background(
            0xA0000000u, 2f,
            Displays.padding(5, mainElement),
        )
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
            add(createDuration("Cookie Buff", EffectsAPI.boosterCookieExpireTime.until(), TextColor.LIGHT_PURPLE))
        }
        if (godPotionInFooter) {
            add(createDuration("God Potion", EffectsAPI.godPotionDuration, TextColor.RED))
        }
        if (SpookyFestivalAPI.onGoing) {
            add(
                Text.join(
                    Text.of("Spooky Festival") {
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
        if (!OverlaysConfig.tablist.enabled) return false
        if (!LocationAPI.isOnSkyBlock) return false
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


    // todo make public in sbapi
    fun <T> List<T>.chunked(predicate: (T) -> Boolean): MutableList<MutableList<T>> {
        val chunks = mutableListOf<MutableList<T>>()
        for (element in this) {
            val currentChunk = chunks.lastOrNull()
            if (currentChunk == null || predicate(element)) {
                chunks.add(mutableListOf(element))
            } else {
                currentChunk.add(element)
            }
        }
        return chunks
    }
}
