package tech.thatgravyboat.skycubed.api

import me.owdding.lib.displays.Display
import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skycubed.utils.fillRect

object ExtraDisplays {
    fun background(color: UInt, radius: Float, border: UInt = 0x0u, display: Display): Display {
        return object : Display {
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                graphics.fillRect(
                    0, 0,
                    getWidth(), getHeight(),
                    color.toInt(),
                    border.toInt(),
                    2,
                    radius.toInt()
                )
                display.render(graphics)
            }
        }
    }

    fun background(color: UInt, radius: Float, display: Display): Display {
        return background(color, radius, color, display)
    }
}