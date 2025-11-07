package tech.thatgravyboat.skycubed.features.tablist

import me.owdding.ktmodules.Module
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.toColumn
import me.owdding.lib.displays.toRow
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.Style
import tech.thatgravyboat.skyblockapi.api.area.hub.SpookyFestivalAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.effects.EffectsAPI
import tech.thatgravyboat.skyblockapi.api.profile.friends.FriendsAPI
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.platform.PlayerSkin
import tech.thatgravyboat.skyblockapi.platform.id
import tech.thatgravyboat.skyblockapi.platform.name
import tech.thatgravyboat.skyblockapi.platform.texture
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.time.until
import tech.thatgravyboat.skycubed.api.ExtraDisplays
import tech.thatgravyboat.skycubed.config.overlays.TabListOverlayConfig
import tech.thatgravyboat.skycubed.features.tablist.Line.Companion.toLine
import tech.thatgravyboat.skycubed.features.tablist.Line.Companion.toLines
import tech.thatgravyboat.skycubed.utils.ContributorHandler
import tech.thatgravyboat.skycubed.utils.Utils.toSkin
import tech.thatgravyboat.skycubed.utils.formatReadableTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

private data class Line(
    val component: Component,
    var face: PlayerSkin? = null,
    var playerName: String? = null,
    var skyblockLevel: Int? = null,
    var extraEmblems: MutableList<Component> = mutableListOf(),
) {
    val stripped: String = component.stripped

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

    private val formattedName = toFormattedName()

    override fun toString() = formattedName
}

@Module
object CompactTablist {

    private var display: Display? = null
    private var lastTablist: List<List<Component>> = emptyList()
    private val titleRegex = "\\s+Info".toRegex()
    private val playerRegex = "\\[(?<level>\\d+)] (?<name>[\\w_-]+).*".toRegex()
    private var boosterCookieInFooter = false
    private var godPotionInFooter = false
    private var filteredFooter: List<FormattedText> = emptyList()

    fun onToggle() {
        if (isEnabled()) {
            createNewDisplay(lastTablist)
        } else {
            display = null
        }
    }

    @Subscription
    fun onTablistUpdate(event: TabListChangeEvent) {
        lastTablist = event.new
        if (!isEnabled()) return
        createNewDisplay(lastTablist)
    }

    @Subscription
    fun onFooterUpdate(event: TabListHeaderFooterChangeEvent) {
        boosterCookieInFooter = event.newFooter.string.contains("\nCookie Buff\n")
        godPotionInFooter = event.newFooter.string.contains("\nYou have a God Potion active!")
        handleLeftOverFooterLines(event.newFooter)
        if (!isEnabled()) return

        createNewDisplay(lastTablist)
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
        playerRegex.match(stripped, "level", "name") { (level, name) ->
            val player = McClient.players.firstOrNull { it.profile.name == name }
            val contributor = ContributorHandler.contributors[player?.profile?.id]

            playerName = name
            face = player?.toSkin()
            skyblockLevel = level.toIntValue()
            // party > coop > friends > guild
            // Todo add apis for this in sbapi
            val isCoop = false
            val isGuild = false
            fun addExtraTag(emblem: String, color: Int) {
                extraEmblems.add(Text.of(emblem) { this.color = color })
            }
            when {
                PartyAPI.members.any { it.name == playerName } -> addExtraTag(
                    "ᴘ",
                    if (PartyAPI.leader?.name == playerName) {
                        TextColor.GOLD
                    } else {
                        TextColor.BLUE
                    },
                )

                isCoop -> addExtraTag("ᴄ", TextColor.AQUA)
                FriendsAPI.friends.any { it.name == playerName } -> addExtraTag("ꜰ", TextColor.GREEN)
                isGuild -> addExtraTag("ɢ", TextColor.DARK_GREEN)
                else -> {}
            }
            contributor?.symbol?.let { extraEmblems.add(Text.of(it)) }
        }
        return this
    }


    private fun createNewDisplay(tablist: List<List<Component>>) {
        if (tablist.isEmpty()) return
        val components: List<List<Line>> = tablist.map { it.toLines() }

        val blocks = mutableListOf<MutableList<Line>>()
        val widgetRegexes = TabWidget.entries.map { it.regex }

        val currentBlock = mutableListOf<Line>()

        fun flushBlock() {
            if (currentBlock.isNotEmpty()) {
                blocks.add(currentBlock.toMutableList())
                currentBlock.clear()
            }
        }

        for ((i, column) in components.withIndex()) {
            for (line in column) {
                val stripped = line.stripped
                if (titleRegex.matches(stripped)) continue
                if (stripped.isBlank()) flushBlock()
                else {
                    if (widgetRegexes.any { it.matches(stripped) }) {
                        flushBlock()
                    }
                    currentBlock.add(line.formatPlayer())
                }
            }
            if (i == 0) flushBlock()
        }
        flushBlock()

        val playerIndexes = mutableListOf<Pair<Int, Int>>()
        val players = mutableListOf<Line>()
        for ((i, block) in blocks.withIndex()) {
            for ((j, line) in block.withIndex()) {
                if (line.playerName == null) continue
                players.add(line)
                playerIndexes.add(i to j)
            }
        }
        players.sortWith(playerComparator)

        for ((index, pos) in playerIndexes.withIndex()) {
            val (blockIndex, lineIndex) = pos
            blocks[blockIndex][lineIndex] = players[index]
        }
        val footer = getFooterSegment().toMutableList()
        if (footer.isNotEmpty()) blocks.addLast(footer)
        val split = blocks.splitParts()

        val mainElement = split.map { segment ->
            segment.map { line ->
                line.face?.let { face ->
                    listOfNotNull(
                        Displays.face({ face.texture!! }),
                        Displays.text(line.component),
                        *line.extraEmblems.map { Displays.text(it) }.toTypedArray(),
                    ).toRow(3)
                } ?: Displays.text(line.component)
            }.toColumn()
        }.toRow(5)

        val footerElement =
            filteredFooter.map { Displays.center(mainElement.getWidth(), display = Displays.text(it)) }.toColumn()

        display = ExtraDisplays.background(
            TabListOverlayConfig.backgroundColor.toUInt(), 2f,
            Displays.padding(5, listOf(mainElement, footerElement).toColumn(5)),
        )
    }

    private val playerComparator = Comparator<Line> { o1, o2 ->
        when (TabListOverlayConfig.sorting) {
            CompactTablistSorting.SKYBLOCK_LEVEL -> o2.skyblockLevel?.compareTo(o1.skyblockLevel ?: 0) ?: 0
            CompactTablistSorting.ALPHABETICAL -> o1.playerName?.compareTo(o2?.playerName ?: "", true) ?: 0
            CompactTablistSorting.FRIENDS -> {
                val o1Friend = o1?.playerName?.let(FriendsAPI::getFriend)
                val o2Friend = o2?.playerName?.let(FriendsAPI::getFriend)
                return@Comparator when {
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

    private fun getFooterSegment(): List<Line> = buildList {
        fun createDuration(
            label: String,
            duration: Duration,
            activeColor: Int,
            inactiveText: String = ": Inactive",
        ) = Text.join(
            Text.of(label) {
                this.color = activeColor
                this.bold = true
            },
            Text.of(
                if (duration.inWholeSeconds > 0) ": ${duration.formatReadableTime(DurationUnit.DAYS, 2)}"
                else inactiveText,
            ).withColor(TextColor.GRAY),
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
                    Text.of(")").withColor(TextColor.GRAY),
                ).toLine(),
            )
        }

        if (size > 1) {
            add(
                0,
                Text.of("Other:") {
                    this.color = TextColor.YELLOW
                    this.bold = true
                }.toLine(),
            )
        }
    }

    fun renderCompactTablist(graphics: GuiGraphics): Boolean {
        if (!isEnabled()) return false
        val display = display ?: return false

        Displays.center(width = graphics.guiWidth(), display = display).render(graphics, 0, 3)

        return true
    }

    private fun List<List<Line>>.splitParts(): List<List<Line>> {
        val totalSize = sumOf { it.size } + size
        val range = TabListOverlayConfig.minColumns..TabListOverlayConfig.maxColumns
        val columns = Math.ceilDiv(totalSize, TabListOverlayConfig.targetColumnSize).let {
            if (range.isEmpty()) it
            else it.coerceIn(range)
        }
        if (columns >= size) return this
        val blockSizes = IntArray(size) { this[it].size }
        val partitions = balanceBlocks(blockSizes, columns)
        var index = 0
        return partitions.map { count ->
            subList(index, index + count).flattenWithSpacing().also { index += count }
        }
    }

    private var lastBlocksData: BlockSizeData? = null

    private class BlockSizeData(
        val blockSizes: IntArray,
        val parts: Int,
        val result: List<Int>
    )

    private fun balanceBlocks(blockSizes: IntArray, parts: Int): List<Int> {
        val cached = lastBlocksData
        if (cached != null) {
            if (cached.blockSizes.contentEquals(blockSizes) && cached.parts == parts) {
                return cached.result
            }
        }
        val cumulativeSums = blockSizes.runningFold(0) { acc, size -> acc + size }
        val maxGroupSize = findMinMaxGroupSize(blockSizes, cumulativeSums, parts)
        val result = partitionBlocks(blockSizes, cumulativeSums, parts, maxGroupSize)
        lastBlocksData = BlockSizeData(blockSizes, parts, result)
        return result
    }

    private fun findMinMaxGroupSize(blocks: IntArray, cumulativeSums: List<Int>, parts: Int): Int {
        val n = blocks.size
        val partitionCost = Array(parts + 1) { IntArray(n + 1) { Int.MAX_VALUE } }
        partitionCost[0][0] = 0

        for (p in 1..parts) {
            for (j in 1..n) {
                for (prev in 0..<j) {
                    val groupSum = cumulativeSums[j] - cumulativeSums[prev]
                    partitionCost[p][j] = minOf(partitionCost[p][j], maxOf(partitionCost[p - 1][prev], groupSum))
                }
            }
        }

        return partitionCost[parts][n]
    }

    private fun partitionBlocks(blocks: IntArray, cumulativeSums: List<Int>, parts: Int, maxSize: Int): List<Int> {
        val result = mutableListOf<Int>()
        var index = 0
        var remaining = parts

        while (remaining > 0 && index < blocks.size) {
            val start = index
            var sum = 0
            val avgTarget = (cumulativeSums.last() - cumulativeSums[index]) / remaining

            while (index < blocks.size) {
                val nextSum = sum + blocks[index]
                val itemsLeft = blocks.size - index - 1
                if (nextSum > maxSize || itemsLeft < remaining - 1 || sum >= avgTarget) break
                sum = nextSum
                index++
            }

            if (index == start) index++
            result += index - start
            remaining--
        }

        return result
    }

    private fun List<List<Line>>.flattenWithSpacing(): List<Line> {
        if (size == 1) return first()
        return flatMapIndexed { i, lines ->
            if (i == lastIndex) lines else lines + Line.EMPTY
        }
    }

    private fun isEnabled() = LocationAPI.isOnSkyBlock && TabListOverlayConfig.enabled
}
