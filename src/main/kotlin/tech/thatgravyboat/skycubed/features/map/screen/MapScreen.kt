package tech.thatgravyboat.skycubed.features.map.screen

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.dropdown.DropdownState
import earth.terrarium.olympus.client.components.textbox.TextBox
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.utils.Orientation
import earth.terrarium.olympus.client.utils.State
import me.owdding.lib.platform.screens.KeyEvent
import me.owdding.lib.platform.screens.MeowddingScreen
import me.owdding.lib.platform.screens.MouseButtonEvent
import me.owdding.lib.utils.matches
import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.dev.MapEditor
import tech.thatgravyboat.skycubed.features.map.pois.Poi
import tech.thatgravyboat.skycubed.utils.ResettingState
import tech.thatgravyboat.skycubed.utils.findFocused

class MapScreen : MeowddingScreen(CommonText.EMPTY) {

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

    override fun keyPressed(keyEvent: KeyEvent): Boolean {
        if ((this.findFocused() as? TextBox)?.isFocused != true) {
            if (Maps.MAP_KEYBIND.matches(keyEvent) || McClient.options.keyInventory.matches(keyEvent)) {
                onClose()
                return true
            }
        }
        return super.keyPressed(keyEvent)
    }

    companion object {

        private fun Poi.filter(search: String): Boolean {
            return search.isEmpty() || this.tooltip.any { it.stripped.contains(search, ignoreCase = true) }
        }
    }

    override fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean): Boolean {
        val widget = this.children().filterIsInstance<MapsWidget>().firstOrNull()
        if (MapEditor.enabled && MapEditorScreen.mouseClicked(widget, mouseEvent.x, mouseEvent.y, mouseEvent.button)) {
            return true
        } else if (MapWaypointsScreen.mouseClicked(widget, mouseEvent.x, mouseEvent.y, mouseEvent.button)) {
            return true
        }
        return super.mouseClicked(mouseEvent, doubleClicked)
    }
}
