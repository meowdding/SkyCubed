package tech.thatgravyboat.skycubed.features.screens

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import earth.terrarium.olympus.client.ui.UIConstants
import me.owdding.lib.displays.DisplayWidget
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.FrameLayout
import tech.thatgravyboat.skyblockapi.utils.text.CommonText

private const val ASPECT_RATIO = 16.0 / 9.0

abstract class BaseUiScreen() : BaseCursorScreen(CommonText.EMPTY) {

    val uiWidth get() = (uiHeight * ASPECT_RATIO).toInt()
    val uiHeight get() = (this.height * 0.7).toInt()

    abstract fun create(bg: DisplayWidget)

    override fun init() {
        val bg = Displays.background(UIConstants.BUTTON.enabled, uiWidth, uiHeight).asWidget()

        FrameLayout.centerInRectangle(bg, 0, 0, this.width, this.height)
        bg.visitWidgets(this::addRenderableOnly)

        create(bg)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBlurredBackground()
        this.renderTransparentBackground(guiGraphics)
    }
}
