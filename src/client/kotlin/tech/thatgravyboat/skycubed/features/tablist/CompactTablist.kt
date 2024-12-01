package tech.thatgravyboat.skycubed.features.tablist

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.api.displays.Display
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.config.Config

typealias Segment = List<Component>

object CompactTablist {

    private var mainElement: Display? = null
    private var footerElement: Display? = null
    private var lastTablist: List<List<Component>> = emptyList()
    private var lastFooter: Component? = null
    private val titleRegex = "\\s+Info".toRegex()
    private val playerRegex = "\\[(?<level>\\d+)] (?<name>[\\w_-]+).*".toRegex()

    @Subscription
    fun onTablistUpdate(event: TabListChangeEvent) {
        createDisplay(event.new, lastFooter ?: CommonText.EMPTY)
    }

    @Subscription
    fun onFooterUpdate(event: TabListHeaderFooterChangeEvent) {
        // TODO: when sbapi is updated, change newHeader to newFooter
        createDisplay(lastTablist, event.newHeader)
    }

    private fun createDisplay(tablist: List<List<Component>>, footer: Component) {
        val segments = tablist.flatMap { it + listOf(CommonText.EMPTY) }
            .map { if (titleRegex.match(it.stripped)) CommonText.EMPTY else it }.chunked { it.string.isBlank() }
            .map { it.filterNot { it.string.isBlank() } }.filterNot(List<Component>::isEmpty)
            .splitListIntoParts(4)
            .map { it.flatMap { it + listOf(CommonText.EMPTY) } }
            .map { list ->
                list.map { component ->
                    var skin: PlayerSkin? = null
                    playerRegex.match(component.string, "level", "name") { (level, name) ->
                        skin = McClient.players.firstOrNull { it.profile.name == name }?.skin
                    }
                    component to skin
                }
            }

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

        mainElement = Displays.row(
            *columns.toTypedArray(),
            spacing = 5
        )

        val split = McFont.self.split(footer, Int.MAX_VALUE)
        footerElement = Displays.column(
            *split.map { Displays.center(mainElement?.getWidth() ?: 0, 10, Displays.text(it)) }.toTypedArray(),
        )

        lastTablist = tablist
        lastFooter = footer
    }

    fun renderCompactTablist(graphics: GuiGraphics): Boolean {
        if (!Config.compactTablist) return false
        val mainElement = mainElement ?: return false
        val footerElement = footerElement ?: return false

        val display = Displays.background(
            0xA0000000u,
            2f,
            Displays.padding(
                5,
                Displays.column(
                    mainElement,
                    footerElement,
                ),
            ),
        )

        val width = McClient.window.guiScaledWidth
        val x = width / 2 - display.getWidth() / 2

        display.render(graphics, x, 3)

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
