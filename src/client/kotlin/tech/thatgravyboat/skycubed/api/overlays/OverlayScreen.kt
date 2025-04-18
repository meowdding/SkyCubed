package tech.thatgravyboat.skycubed.api.overlays

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.config.ConfigManager

class OverlayScreen(private val overlay: Overlay) : Screen(CommonText.EMPTY) {

    private var dragging = false
    private var relativeX = 0
    private var relativeY = 0

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(graphics, mouseX, mouseY, partialTicks)
        val (x, y) = overlay.position
        val (width, height) = overlay.bounds * overlay.position.scale

        val hovered = mouseX - x in 0..width && mouseY - y in 0..height
        graphics.pushPop {
            translate(x.toFloat(), y.toFloat(), 0f)
            scale(overlay.position.scale, overlay.position.scale, 1f)
            overlay.render(graphics, mouseX, mouseY)
        }
        if (hovered) {
            graphics.fill(x, y, x + width, y + height, 0x50000000)
            graphics.renderOutline(x - 1, y - 1, width + 2, height + 2, 0xFFFFFFFF.toInt())
            setTooltipForNextRenderPass(Text.multiline(
                overlay.name,
                CommonText.EMPTY,
                Component.translatable("ui.skycubed.overlay.edit.options")
            ))
        }

        graphics.drawCenteredString(font, "X: ${overlay.position.x}, Y: ${overlay.position.y}", (this.width / 2f).toInt(), this.height - 30, -1)
        graphics.drawCenteredString(font, "Scale: ${overlay.position.scale}", (this.width / 2f).toInt(), this.height - 20, -1)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, i: Int, f: Double, g: Double): Boolean {
        if (dragging) {
            overlay.setX(mouseX.toInt() - relativeX)
            overlay.setY(mouseY.toInt() - relativeY)
        }
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        dragging = false
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val (x, y) = overlay.position
        val (width, height) = overlay.bounds * overlay.position.scale

        if ((mouseX - x).toInt() in 0..width && (mouseY - y).toInt() in 0..height) {
            when (button) {
                InputConstants.MOUSE_BUTTON_LEFT -> {
                    relativeX = (mouseX - x).toInt()
                    relativeY = (mouseY - y).toInt()
                    dragging = true
                }
                InputConstants.MOUSE_BUTTON_RIGHT -> {
                    overlay.onRightClick()
                }
            }
        }
        return true
    }

    override fun keyPressed(key: Int, scan: Int, modifiers: Int): Boolean {
        val multiplier = if (hasShiftDown()) 10 else 1
        val (x, y) = overlay.position
        when (key) {
            InputConstants.KEY_UP -> overlay.setY(y - multiplier)
            InputConstants.KEY_DOWN -> overlay.setY(y + multiplier)
            InputConstants.KEY_LEFT -> overlay.setX(x - multiplier)
            InputConstants.KEY_RIGHT -> overlay.setX(x + multiplier)
            InputConstants.KEY_EQUALS -> overlay.position.scale += 0.1f
            InputConstants.KEY_MINUS -> overlay.position.scale -= 0.1f
            else -> return super.keyPressed(key, scan, modifiers)
        }
        return true
    }

    override fun onClose() {
        super.onClose()
        ConfigManager.save()
    }

    companion object {
        fun inScreen() = McScreen.self is OverlayScreen
    }
}

private operator fun Pair<Int, Int>.times(scale: Float): Pair<Int, Int> {
    return (first * scale).toInt() to (second * scale).toInt()
}
