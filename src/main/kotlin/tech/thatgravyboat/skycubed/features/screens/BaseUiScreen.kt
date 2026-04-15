package tech.thatgravyboat.skycubed.features.screens

import earth.terrarium.olympus.client.ui.UIConstants
import me.owdding.lib.displays.DisplayWidget
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asWidget
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.platform.applyBackgroundBlur
import tech.thatgravyboat.skyblockapi.utils.text.CommonText

private const val ASPECT_RATIO = 16.0 / 9.0

abstract class BaseUiScreen() : Screen(CommonText.EMPTY) {

    val uiWidth get() = (uiHeight * ASPECT_RATIO).toInt()
    val uiHeight get() = (this.height * 0.7).toInt()

    abstract fun create(bg: DisplayWidget)

    override fun init() {
        val bg = Displays.background(UIConstants.BUTTON.enabled(), uiWidth, uiHeight).asWidget()

        FrameLayout.centerInRectangle(bg, 0, 0, this.width, this.height)
        bg.visitWidgets(this::addRenderableOnly)

        create(bg)
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.applyBackgroundBlur()
        this.extractTransparentBackground(graphics)
    }
}
