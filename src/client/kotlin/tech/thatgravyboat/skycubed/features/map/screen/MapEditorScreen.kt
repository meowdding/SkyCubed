package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.platform.InputConstants
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.dropdown.DropdownState
import earth.terrarium.olympus.client.ui.context.ContextMenu
import earth.terrarium.olympus.client.ui.modals.ActionModal
import earth.terrarium.olympus.client.ui.modals.Modals
import org.joml.Vector3i
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skycubed.api.conditions.Condition
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.dev.MapPoiEditScreen
import tech.thatgravyboat.skycubed.features.map.pois.ConditionalPoi
import tech.thatgravyboat.skycubed.features.map.pois.Poi

object MapEditorScreen {

    fun mouseClicked(widget: MapsWidget?, mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
            ContextMenu.open(mouseX, mouseY) {
                val element = widget?.getPoiAt(mouseX, mouseY)
                if (element != null) {
                    it.dangerButton(Text.of("Delete Poi")) {
                        widget.removePoi(element.first)
                    }
                }

                it.button(Text.of("New Poi")) {
                    val state = DropdownState.empty<String>()
                    Modals.action()
                        .withTitle(Text.of("Select Poi Type"))
                        .withContent { width ->
                            Widgets.dropdown(
                                state,
                                Poi.poiTypes.toMutableList().apply { this.add("insignificant_npc") },
                                { Text.of(it.toString()) },
                                {},
                            ) {
                                it.withCallback { poi ->
                                    if (McClient.self.screen is ActionModal) {
                                        McClient.self.screen?.onClose()
                                    }

                                    val newPoi = if (poi == "insignificant_npc") {
                                        val npc = Poi.createByType("npc", Vector3i()) ?: return@withCallback
                                        ConditionalPoi(Condition.TRUE, Condition.FALSE, npc)
                                    } else {
                                        Poi.createByType(poi, Vector3i()) ?: return@withCallback
                                    }

                                    val pois = Maps.currentIsland?.pois ?: run {
                                        Text.of("Unknown island").send()
                                        return@withCallback
                                    }
                                    pois.add(newPoi)
                                    val current = McScreen.self
                                    McClient.setScreenAsync { MapPoiEditScreen(newPoi, pois, current) }
                                }
                            }.withSize(width, 20)
                        }.apply {
                            McClient.runNextTick { open() }
                        }
                }
            }
            return true
        }
        return false
    }
}
