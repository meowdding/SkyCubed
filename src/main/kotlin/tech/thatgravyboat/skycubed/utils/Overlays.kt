package tech.thatgravyboat.skycubed.utils

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
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
    val background: OverlayBackgroundConfig get() = OverlayBackgroundConfig.TEXTURED
    val actualBounds: Pair<Int, Int>
    override val bounds: Pair<Int, Int>
        get() {
            if (background == OverlayBackgroundConfig.NO_BACKGROUND) return actualBounds
            val (width, height) = actualBounds
            return (width + 8) to (height + 8)
        }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val offset = when (background) {
            OverlayBackgroundConfig.TEXTURED -> {
                graphics.drawSprite(SkyCubedTextures.backgroundBox, 0, 0, bounds.first, bounds.second)
                4
            }

            OverlayBackgroundConfig.TRANSLUCENT -> {
                graphics.fill(0, 0, bounds.first, bounds.second, 0x50000000)
                4
            }

            OverlayBackgroundConfig.NO_BACKGROUND -> 0
        }

        graphics.translated(offset, offset) {
            renderWithBackground(graphics, mouseX - offset, mouseY - offset, partialTicks)
        }
    }

    fun renderWithBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) = renderWithBackground(graphics, mouseX, mouseY)
    fun renderWithBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int) = render(graphics, mouseX, mouseY)
}

interface BackgroundLessSkyCubedOverlay : SkyCubedOverlay {
    override val bounds: Pair<Int, Int> get() = actualBounds
    override val background: OverlayBackgroundConfig get() = OverlayBackgroundConfig.NO_BACKGROUND
}

enum class OverlayBackgroundConfig : Translatable {
    TEXTURED,
    TRANSLUCENT,
    NO_BACKGROUND;

    override fun getTranslationKey(): String = "skycubed.config.overlays.background.${this.name.lowercase()}"
}
