package tech.thatgravyboat.skycubed.features.screens

import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.olympus.client.utils.ListenableState
import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.builder.LayoutBuilder
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.displays.*
import me.owdding.lib.layouts.setPos
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.RepoAttributeAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skycubed.api.ExtraDisplays
import tech.thatgravyboat.skycubed.config.ConfigManager
import tech.thatgravyboat.skycubed.config.overlays.AttributeOverlayConfig

class AttributeHudEditScreen : BaseUiScreen("Attribute Hud Editor") {

    val leftState: ListenableState<String> = ListenableState.of("")
    val rightState: ListenableState<String> = ListenableState.of("")
    var leftSearch = ""
    var rightSearch = ""

    var selectedItems: List<SkyBlockId>
        get() = AttributeOverlayConfig.attributes.map { SkyBlockId.attribute(it) }
        private set(value) {
            AttributeOverlayConfig.attributes = value.map { it.cleanId }.toTypedArray()
            ConfigManager.save()
        }

    override fun create(bg: DisplayWidget) {
        val columnWidth = (uiWidth - 13) / 2
        LayoutFactory.horizontal {
            vertical(5, 0.5f) {
                string("Selected Attributes")

                addItems(true, leftState, columnWidth, selectedItems)

                spacer(width = columnWidth)
            }
            display(ExtraDisplays.background(0xA0000000u, 2f, Displays.empty(6, uiHeight - 10)))
            vertical(5, 0.5f) {
                string("Search Attributes")
                val allIds = RepoAPI.attributes().attributes().values.map { SkyBlockId.attribute(it.attributeId.lowercase()) }

                addItems(false, rightState, columnWidth, allIds)

                spacer(width = columnWidth)
            }
        }.setPos(bg.x + 5, bg.y + 5).visitWidgets(this::addRenderableWidget)
    }

    private fun LayoutBuilder.addItems(left: Boolean, state: ListenableState<String>, columnWidth: Int, itemIds: List<SkyBlockId>) {
        val items = itemIds.associateWith { it.toItem() }
        val list = ListWidget(columnWidth - 20, uiHeight - 55)

        fun updateList(input: String) {
            if (left) {
                leftSearch = input
            } else {
                rightSearch = input
            }
            list.clear()
            if (input.isEmpty()) {
                items
            } else {
                items.filter { (id, item) ->
                    listOfNotNull(
                        RepoAttributeAPI.getAttributeDataById(id.cleanId)?.name,
                        id.skyblockId,
                        item.cleanName,
                    ).any { it.contains(input, true) }
                }
            }.onEachIndexed { i, (k, v) ->
                val color = if (i % 2 == 0) 0xFFA1A3A3u else 0xFFC7C6C9u
                val display = DisplayFactory.vertical {
                    spacer(width = columnWidth - 40)
                    horizontal(5, Alignment.CENTER) {
                        display(Displays.item(v))
                        string(v.hoverName)
                    }
                }

                ExtraDisplays.background(color, 1f, display.withPadding(2)).asButtonLeft {
                    if (selectedItems.contains(k)) {
                        selectedItems = selectedItems.filter { it != k }
                        rebuildWidgets()
                    } else {
                        selectedItems += k
                        rebuildWidgets()
                    }
                }.let {
                    list.add(it)
                }
            }
        }

        textInput(
            state = state,
            placeholder = "Search...",
            width = columnWidth - 10,
            onChange = { updateList(it) },
        )

        widget(list)
        if (left) updateList(leftSearch) else updateList(rightSearch)
    }

    @Module
    companion object {
        @Subscription
        fun onCommand(event: RegisterCommandsEvent) {
            event.registerWithCallback("skycubed attributehud") {
                McClient.setScreen(AttributeHudEditScreen())
            }
        }
    }
}
