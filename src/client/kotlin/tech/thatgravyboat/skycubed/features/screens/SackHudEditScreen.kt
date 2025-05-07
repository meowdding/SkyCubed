package tech.thatgravyboat.skycubed.features.screens

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.olympus.client.utils.ListenableState
import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.builder.LayoutBuilder
import me.owdding.lib.builder.LayoutBuilder.Companion.setPos
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.displays.Alignment
import me.owdding.lib.displays.DisplayWidget
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asButton
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skycubed.api.ExtraDisplays
import tech.thatgravyboat.skycubed.api.repo.SackCodecs
import tech.thatgravyboat.skycubed.config.overlays.SackOverlay

class SackHudEditScreen : BaseUiScreen() {

    val leftState: ListenableState<String> = ListenableState.of("")
    val rightState: ListenableState<String> = ListenableState.of("")
    var leftSearch = ""
    var rightSearch = ""

    var selectedItems: List<String>
        get() = SackOverlay.sackItems.toMutableList()
        private set(value) {
            SackOverlay.sackItems = value.toTypedArray()
        }

    override fun create(bg: DisplayWidget) {
        val columnWidth = (uiWidth - 13) / 2
        LayoutFactory.horizontal {
            vertical(5, 0.5f) {
                string("Selected Items")

                addItems(true,  leftState, columnWidth, selectedItems.associateWith { SackCodecs.sackItems[it] ?: Items.BARRIER.defaultInstance })

                spacer(width = columnWidth)
            }
            display(ExtraDisplays.background(0xA0000000u, 2f, Displays.empty(6, uiHeight - 10)))
            vertical(5, 0.5f) {
                string("Search Items")

                addItems(false, rightState, columnWidth, SackCodecs.sackItems)

                spacer(width = columnWidth)
            }
        }.setPos(bg.x + 5, bg.y + 5).visitWidgets(this::addRenderableWidget)
    }

    private fun LayoutBuilder.addItems(left: Boolean, state: ListenableState<String>, columnWidth: Int, items: Map<String, ItemStack>) {
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
                items.filter { (k, v) ->
                    k.lowercase().contains(input, true) || v.cleanName.contains(input, true)
                }
            }.forEach { (k, v) ->
                DisplayFactory.horizontal(alignment = Alignment.CENTER) {
                    display(Displays.item(v))
                    string(v.hoverName)
                }.asButton {
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
            event.register("sackhud") {
                callback {
                    McClient.setScreen(SackHudEditScreen())
                }
            }
        }
    }

    // TODO: move into mlib
    fun LayoutBuilder.textInput(
        state: ListenableState<String>,
        placeholder: String = "",
        width: Int,
        height: Int = 20,
        onChange: (String) -> Unit = {},
        onEnter: (String) -> Unit = {},
    ) {
        val input = Widgets.textInput(state) { box ->
            box.withEnterCallback {
                onEnter(box.value)
            }
            box.withChangeCallback {
                onChange(it)
            }
        }
        input.withPlaceholder(placeholder)
        input.withSize(width, height)

        widget(input)
    }
}
