package tech.thatgravyboat.skycubed.utils

import me.owdding.ktmodules.AutoCollect
import me.owdding.lib.overlays.Overlay
import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skycubed.SkyCubed

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@AutoCollect("RegisteredOverlays")
annotation class RegisterOverlay

interface SkyCubedOverlay : Overlay {
    override val modId: String get() = SkyCubed.MOD_ID
    val background: OverlayBackground get() = OverlayBackground.TEXTURED

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        when (background) {
            OverlayBackground.TEXTURED -> {}
            OverlayBackground.COLORED -> {}
            OverlayBackground.NO_BACKGROUND -> {}
        }
        renderWithBackground(graphics, mouseX, mouseY, partialTicks)
    }

    fun renderWithBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) = renderWithBackground(graphics, mouseX, mouseY)
    fun renderWithBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int)
}

enum class OverlayBackground {
    TEXTURED,
    COLORED,
    NO_BACKGROUND;
}
