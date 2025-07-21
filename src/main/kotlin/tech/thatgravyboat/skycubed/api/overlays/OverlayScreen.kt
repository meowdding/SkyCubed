package tech.thatgravyboat.skycubed.api.overlays

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.lib.utils.keys
import me.owdding.lib.utils.keysOf
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.pushPop
import tech.thatgravyboat.skyblockapi.platform.scale
import tech.thatgravyboat.skyblockapi.platform.showTooltip
import tech.thatgravyboat.skyblockapi.platform.translate
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.config.ConfigManager

private val ADD_KEY = keys {
    withSymbol("+")
    withKey(InputConstants.KEY_EQUALS)
    withKey(InputConstants.KEY_ADD)
}

private val MINUS_KEY = keys {
    withSymbol("-")
    withKey(InputConstants.KEY_MINUS)
    withKey(InputConstants.KEY_ADD)
}

private val UP_KEY = keysOf(InputConstants.KEY_UP)
private val DOWN_KEY = keysOf(InputConstants.KEY_DOWN)
private val LEFT_KEY = keysOf(InputConstants.KEY_LEFT)
private val RIGHT_KEY = keysOf(InputConstants.KEY_RIGHT)

class OverlayScreen(private val overlay: Overlay, private val parent: Screen?) : Screen(CommonText.EMPTY) {

    private var dragging = false
    private var relativeX = 0
    private var relativeY = 0

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.render(graphics, mouseX, mouseY, partialTicks)
        val (x, y) = overlay.position
        val (width, height) = overlay.bounds * overlay.position.scale

        val hovered = mouseX - x in 0..width && mouseY - y in 0..height
        graphics.pushPop {
            graphics.translate(x.toFloat(), y.toFloat())
            graphics.scale(overlay.position.scale, overlay.position.scale)
            overlay.render(graphics, mouseX, mouseY, partialTicks)
        }
        if (hovered) {
            graphics.fill(x, y, x + width, y + height, 0x50000000)
            graphics.renderOutline(x - 1, y - 1, width + 2, height + 2, 0xFFFFFFFF.toInt())
            graphics.showTooltip(Text.multiline(
                overlay.name,
                CommonText.EMPTY,
                Component.translatable("skycubed.ui.overlay.edit.options"),
            ))
        }

        val center = (this.width / 2f).toInt()
        graphics.drawCenteredString(font, "X: ${overlay.position.x}, Y: ${overlay.position.y}", center, this.height - 40, -1)
        graphics.drawCenteredString(font, "Scale: ${overlay.position.scale}", center, this.height - 30, -1)
        graphics.drawCenteredString(font, "Use +/- to scale, arrow keys to move around.", center, this.height - 20, -1)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderMenuBackground(guiGraphics)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, i: Int, f: Double, g: Double): Boolean {
        if (dragging) {
            if (EditableProperty.X in overlay.properties) overlay.setX(mouseX.toInt() - relativeX)
            if (EditableProperty.Y in overlay.properties) overlay.setY(mouseY.toInt() - relativeY)
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
        } else if (this.parent != null) {
            ConfigManager.save()
            McClient.setScreen(this.parent)
        }
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        if (isMouseOverOverlay(mouseX, mouseY) && EditableProperty.SCALE in overlay.properties) {
            val scale = overlay.position.scale + scrollY * 0.1f
            overlay.setScale(scale.toFloat())
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun keyPressed(key: Int, scan: Int, modifiers: Int): Boolean {
        val multiplier = if (hasShiftDown()) 10 else 1
        val (x, y) = overlay.position
        val scale = overlay.position.scale

        when {
            UP_KEY.isDown(key, scan) && EditableProperty.Y in overlay.properties -> overlay.setY(y - multiplier)
            DOWN_KEY.isDown(key, scan) && EditableProperty.Y in overlay.properties -> overlay.setY(y + multiplier)
            LEFT_KEY.isDown(key, scan) && EditableProperty.X in overlay.properties -> overlay.setX(x - multiplier)
            RIGHT_KEY.isDown(key, scan) && EditableProperty.X in overlay.properties -> overlay.setX(x + multiplier)
            ADD_KEY.isDown(key, scan) && EditableProperty.SCALE in overlay.properties -> overlay.setScale(scale + 0.1f)
            MINUS_KEY.isDown(key, scan) && EditableProperty.SCALE in overlay.properties -> overlay.setScale(scale - 0.1f)
            else -> return super.keyPressed(key, scan, modifiers)
        }
        return true
    }

    override fun onClose() {
        ConfigManager.save()

        if (parent != null && parent !is ChatScreen) {
            McClient.setScreen(parent)
        } else {
            super.onClose()
        }
    }

    fun isMouseOverOverlay(mouseX: Double, mouseY: Double): Boolean {
        val (x, y) = overlay.position
        val (width, height) = overlay.bounds * overlay.position.scale
        return ((mouseX - x).toInt() in 0..width && (mouseY - y).toInt() in 0..height)
    }
}

private operator fun Pair<Int, Int>.times(scale: Float): Pair<Int, Int> {
    return (first * scale).toInt() to (second * scale).toInt()
}
