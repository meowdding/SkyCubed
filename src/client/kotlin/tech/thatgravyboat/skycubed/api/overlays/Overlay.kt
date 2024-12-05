package tech.thatgravyboat.skycubed.api.overlays

import earth.terrarium.olympus.client.ui.context.ContextMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.utils.Rect

interface Overlay {

    val name: Component

    val moveable: Boolean get() = true
    val enabled: Boolean get() = true

    val position: Position
    val bounds: Pair<Int, Int>
    val editBounds: Rect get() {
        val (x, y) = position
        return Rect(x, y, bounds.first, bounds.second)
    }

    fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int)

    fun onRightClick() = ContextMenu.open {
        it.dangerButton(Text.of("Reset Position")) {
            position.reset()
        }
    }

    fun setX(x: Int) {
        val width = McClient.window.guiScaledWidth
        if (bounds.first == 0 || bounds.first >= width) return
        position.x = if (x < width / 2) x.coerceAtLeast(0) else (x - width).coerceAtMost(-bounds.first)
    }

    fun setY(y: Int) {
        val height = McClient.window.guiScaledHeight
        if (bounds.second == 0 || bounds.second >= height) return
        position.y = if (y < height / 2) y.coerceAtLeast(0) else (y - height).coerceAtMost(-bounds.second)
    }
}