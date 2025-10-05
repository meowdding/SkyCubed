package tech.thatgravyboat.skycubed.features.overlays.map

import earth.terrarium.olympus.client.ui.context.ContextMenu
import earth.terrarium.olympus.client.utils.State
import me.owdding.ktmodules.Module
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.overlays.ConfigPosition
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.config.overlays.MapOverlayConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.Maps.getMapsForLocationOrNull
import tech.thatgravyboat.skycubed.features.map.screen.MapShape
import tech.thatgravyboat.skycubed.features.map.screen.MapsWidget
import tech.thatgravyboat.skycubed.utils.GettingState
import tech.thatgravyboat.skycubed.utils.RegisterOverlay
import tech.thatgravyboat.skycubed.utils.SkyCubedOverlay
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures.backgroundBox
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures.backgroundCircle

@Module
@RegisterOverlay
object MinimapOverlay : SkyCubedOverlay {
    override val name: Component = Text.of("Minimap")
    override val position: ConfigPosition = OverlayPositions.map
    override val bounds: Pair<Int, Int> = 90 to 90
    override val enabled: Boolean get() = LocationAPI.isOnSkyBlock && (display != null && MapOverlayConfig.enabled) || DungeonMapOverlay.canRender

    private var display: Display? = null

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (display != null && MapOverlayConfig.enabled) {
            display!!.render(graphics)
        } else if (DungeonMapOverlay.canRender) {
            DungeonMapOverlay.render(graphics, partialTicks)
        }
    }

    override fun onRightClick() = ContextMenu.open {
        it.button(Text.of("${if (MapOverlayConfig.rotateAroundPlayer) "Disable" else "Enable"} Rotation")) {
            MapOverlayConfig.rotateAroundPlayer = !MapOverlayConfig.rotateAroundPlayer
        }
        it.button(Text.of("Change to ${MapOverlayConfig.mapShape.next.displayName}")) {
            MapOverlayConfig.mapShape = MapOverlayConfig.mapShape.next
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.resetPosition()
        }
    }

	@Subscription(IslandChangeEvent::class)
    fun updateDisplay() {
        display = getMapsForLocationOrNull()?.let {
            val minimapWidget = Displays.center(
                90, 90,
                Displays.renderable(
                    MapsWidget(
                        it,
                        GettingState.of { McPlayer.position!!.x + Maps.getCurrentOffset().x },
                        GettingState.of { McPlayer.position!!.z + Maps.getCurrentOffset().z },
                        State.of(1f),
                        { false },
                        86,
                        86,
                        GettingState.of { MapOverlayConfig.rotateAroundPlayer },
                        MapOverlayConfig.mapShape,
                    ),
                ),
            )

            when (MapOverlayConfig.mapShape) {
                MapShape.SQUARE -> Displays.background(
                    backgroundBox,
                    minimapWidget
                )
                MapShape.CIRCLE -> Displays.layered(
                    Displays.circleTexture(90, 90, backgroundCircle),
                    minimapWidget,
                )
            }
        }
    }
}
