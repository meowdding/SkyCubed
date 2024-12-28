package tech.thatgravyboat.skycubed.features.overlays.map

import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.api.displays.Display
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.Maps.getMapsForLocationOrNull
import tech.thatgravyboat.skycubed.features.map.screen.MapsWidget
import tech.thatgravyboat.skycubed.utils.GettingState

object MinimapOverlay : Overlay {

    override val name: Component = Text.of("Minimap")
    override val position: Position = OverlayPositions.map
    override val bounds: Pair<Int, Int> = 90 to 90
    override val enabled: Boolean get() = (display != null || DungeonMap.canRender) && OverlaysConfig.map.enabled

    private var display: Display? = null

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        if (display != null) {
            display!!.render(graphics)
        } else {
            DungeonMap.render(graphics)
        }
    }

    @Subscription
    fun onChange(event: IslandChangeEvent) {
        display = getMapsForLocationOrNull()?.let {
            Displays.background(
                SkyCubed.id("background"),
                Displays.background(
                    0xFF0000FFu,
                    0f,
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
            )
        }
    }
}