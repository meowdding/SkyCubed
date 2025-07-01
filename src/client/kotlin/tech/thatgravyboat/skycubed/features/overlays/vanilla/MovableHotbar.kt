package tech.thatgravyboat.skycubed.features.overlays.vanilla

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.api.overlays.RegisterOverlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.overlays.Position

@RegisterOverlay
object MovableHotbar : Overlay {
    override val name: Component = Text.of("Moveable Hotbar")
    override val enabled: Boolean get() = OverlaysConfig.movableHotbar && LocationAPI.isOnSkyBlock
    override val position: Position = OverlayPositions.hotbar
    override val bounds: Pair<Int, Int> = 182 to 22

    /**
     * Handling happens in [tech.thatgravyboat.skycubed.mixins.GuiMixin]
     */
    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
    }

}
