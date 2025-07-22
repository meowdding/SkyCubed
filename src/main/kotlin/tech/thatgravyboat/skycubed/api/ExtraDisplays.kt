package tech.thatgravyboat.skycubed.api

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.platform.drawRoundedRectangle
import net.minecraft.client.gui.GuiGraphics

object ExtraDisplays {
    fun background(color: UInt, radius: Float, border: UInt = 0x0u, display: Display): Display {
        return object : Display {
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                graphics.drawRoundedRectangle(0, 0, getWidth(), getHeight(), color, border, radius, 2)
                display.render(graphics)
            }
        }
    }

    fun background(color: UInt, radius: Float, display: Display): Display {
        return background(color, radius, color, display)
    }

    fun missingTextureDisplay(width: Int = 8, height: Int = 8): Display {
        fun filledDisplay(color: UInt) = Displays.background(color, Displays.empty(width / 2, height / 2))
        return Displays.outline(
            { 0xFFFFFFFFu },
            Displays.column(
                Displays.row(filledDisplay(0xFFFF00FFu), filledDisplay(0xFF000000u)),
                Displays.row(filledDisplay(0xFF000000u), filledDisplay(0xFFFF00FFu)),
            ),
        )
    }

    fun custom(width: Int, height: Int, graphics: GuiGraphics.() -> Unit): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height

            override fun render(graphics: GuiGraphics) = graphics.graphics()

        }
    }
}
