package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.platform.InputConstants
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.dropdown.DropdownState
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.ui.context.ContextMenu
import earth.terrarium.olympus.client.ui.modals.ActionModal
import earth.terrarium.olympus.client.ui.modals.Modals
import earth.terrarium.olympus.client.utils.Orientation
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.GuiGraphics
import org.joml.Vector3i
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.api.conditions.Condition
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.dev.MapEditor
import tech.thatgravyboat.skycubed.features.map.dev.MapPoiEditScreen
import tech.thatgravyboat.skycubed.features.map.pois.ConditionalPoi
import tech.thatgravyboat.skycubed.features.map.pois.Poi
import tech.thatgravyboat.skycubed.utils.ResettingState

class MapScreen : BaseCursorScreen(CommonText.EMPTY) {

    private val search = State.of("")

    private val map = DropdownState.of(Maps.getMapsForLocation())
    private var lastMap = map.get()

    private val xOffset = ResettingState.of {
        if (map.get() == Maps.getMapsForLocationOrNull()) {
            McPlayer.self!!.position().x + Maps.getCurrentOffset().x
        } else {
            val maps = Maps.getMaps(map.get())
            val min = maps.minOfOrNull { it.topX } ?: 0
            val max = maps.maxOfOrNull { it.bottomX } ?: 0
            min + (max - min) / 2.0
        }
    }
    private val zOffset = ResettingState.of {
        if (map.get() == Maps.getMapsForLocationOrNull()) {
            McPlayer.self!!.position().z + Maps.getCurrentOffset().z
        } else {
            val maps = Maps.getMaps(map.get())
            val min = maps.minOfOrNull { it.topY } ?: 0
            val max = maps.maxOfOrNull { it.bottomY } ?: 0
            min + (max - min) / 2.0
        }
    }
    private val scale = ResettingState.of {
        1f
    }

    override fun init() {
        if (lastMap != map.get()) {
            xOffset.reset()
            zOffset.reset()
            scale.reset()
            lastMap = map.get()
        }

        Layouts.column().withChild(
            Widgets.frame {
                it.withTexture(UIConstants.MODAL_HEADER)
                it.withSize(this.width, 30)
                it.withContentMargin(5)
                it.withEqualSpacing(Orientation.HORIZONTAL)

                it.withContents { contents ->
                    contents.addChild(
                        Widgets.textInput(search) { input ->
                            input.withPlaceholder("Search POIs...")
                            input.withSize(150, 20)
                        },
                    )

                    contents.addChild(
                        Widgets.dropdown(
                            map,
                            Maps.getMaps(),
                            { map -> Text.translatable("skycubed.map.$map") },
                            { button -> button.withSize(150, 20) },
                            { },
                        ),
                    )
                }
            },
        ).withChild(
            MapsWidget(
                map = map.get(),
                xOffset = xOffset,
                zOffset = zOffset,
                scale = scale,
                filter = { it.enabled && it.filter(search.get()) && (search.get().isNotEmpty() || it.significant) },
                width = this.width,
                height = this.height - 30,
            ),
        ).build(this::addRenderableWidget)
    }

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderTransparentBackground(graphics)
    }

    companion object {

        private fun Poi.filter(search: String): Boolean {
            return search.isEmpty() || this.tooltip.any { it.stripped.contains(search, ignoreCase = true) }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == InputConstants.MOUSE_BUTTON_RIGHT && MapEditor.enabled) {
            ContextMenu.open(mouseX, mouseY) {
                val map = this.children().filterIsInstance<MapsWidget>().firstOrNull()
                val poi = map?.getElementUnder(mouseX, mouseY)
                if (poi != null) {
                    it.dangerButton(Text.of("Delete Poi")) {
                        map.removePoi(poi)
                    }
                }

                it.button(Text.of("New Poi")) {
                    val state = DropdownState.empty<String>()
                    Modals.action()
                        .withTitle(Text.of("Select Poi Type"))
                        .withContent { width ->
                            Widgets.dropdown(
                                state,
                                Poi.poiTypes.toList().let {
                                    it.toMutableList().apply { this.add("insignificant_npc") }
                                },
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

                                    Maps.currentIsland?.pois?.add(newPoi)
                                    McClient.setScreenAsync { MapPoiEditScreen(newPoi, this) }
                                }
                            }.withSize(width, 20)
                        }.apply {
                            McClient.runNextTick { open() }
                        }
                }
            }
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}
