package tech.thatgravyboat.skycubed.features.overlays.map

import earth.terrarium.olympus.client.utils.State
import me.owdding.ktmodules.Module
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.api.overlays.RegisterOverlay
import tech.thatgravyboat.skycubed.config.overlays.MapOverlayConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.Maps.getMapsForLocationOrNull
import tech.thatgravyboat.skycubed.features.map.screen.MapsWidget
import tech.thatgravyboat.skycubed.utils.GettingState
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures.backgroundBox

@Module
@RegisterOverlay
object MinimapOverlay : Overlay {

    override val name: Component = Text.of("Minimap")
    override val position: Position = OverlayPositions.map
    override val bounds: Pair<Int, Int> = 90 to 90
    override val enabled: Boolean get() = (display != null && MapOverlayConfig.enabled) || DungeonMapOverlay.canRender

    private var display: Display? = null

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        if (display != null && MapOverlayConfig.enabled) {
            display!!.render(graphics)
        } else if (DungeonMapOverlay.canRender) {
            DungeonMapOverlay.render(graphics)
        }
    }

    @Subscription
    fun onChange(event: IslandChangeEvent) {
        display = getMapsForLocationOrNull()?.let {
            Displays.background(
                backgroundBox,
                Displays.center(90, 90, Displays.renderable(MapsWidget(
                    it,
                    GettingState.of { McPlayer.self!!.blockPosition().x + Maps.getCurrentOffset().x },
                    GettingState.of { McPlayer.self!!.blockPosition().z + Maps.getCurrentOffset().z },
                    State.of(1f),
                    { false },
                    86,
                    86
                )))
            )
        }
    }
}
