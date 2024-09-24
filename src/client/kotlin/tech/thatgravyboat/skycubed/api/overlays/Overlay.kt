package tech.thatgravyboat.skycubed.api.overlays

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.config.Position

interface Overlay {

    val name: Component

    val enabled: Boolean
        get() = true

    val position: Position
    val bounds: Pair<Int, Int>

    fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int)

    fun setX(x: Int) {
        val width = McClient.window.guiScaledWidth
        position.x = if (x < width / 2) x.coerceAtLeast(0) else (x - width).coerceAtMost(-bounds.first)
    }

    fun setY(y: Int) {
        val height = McClient.window.guiScaledHeight
        position.y = if (y < height / 2) y.coerceAtLeast(0) else (y - height).coerceAtMost(-bounds.second)
    }
}