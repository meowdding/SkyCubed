package tech.thatgravyboat.skycubed.utils

import me.owdding.ktmodules.AutoCollect
import me.owdding.lib.overlays.Overlay
import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.platform.drawSprite
import tech.thatgravyboat.skyblockapi.utils.extentions.translated
import tech.thatgravyboat.skycubed.SkyCubed

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@AutoCollect("RegisteredOverlays")
annotation class RegisterOverlay

interface SkyCubedOverlay : Overlay {
    override val modId: String get() = SkyCubed.MOD_ID
    val background: OverlayBackground get() = OverlayBackground.TEXTURED

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val offset = when (background) {
            OverlayBackground.TEXTURED -> {
                graphics.drawSprite(SkyCubedTextures.backgroundBox, 0, 0, bounds.first, bounds.second)
                4
            }

            OverlayBackground.COLORED -> {
                graphics.fill(0, 0, bounds.first, bounds.second, 0x50000000)
                4
            }

            OverlayBackground.NO_BACKGROUND -> 0
        }

        graphics.translated(offset, offset) {
            renderWithBackground(graphics, mouseX + offset, mouseY + offset, partialTicks)
        }
    }

    fun renderWithBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) = renderWithBackground(graphics, mouseX, mouseY)
    fun renderWithBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {}
}

enum class OverlayBackground {
    TEXTURED,
    COLORED,
    NO_BACKGROUND;
}
