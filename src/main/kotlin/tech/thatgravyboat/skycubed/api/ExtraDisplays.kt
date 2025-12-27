package tech.thatgravyboat.skycubed.api

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.platform.drawRoundedRectangle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.world.item.ItemStack
//? > 1.21.5
import tech.thatgravyboat.skycubed.utils.SpinningItemRenderState

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

    fun spinningItem(item: ItemStack, xSpeed: Int = 0, ySpeed: Int = 0, zSpeed: Int = 0, scale: Float = 1f): Display = object : Display {
        override fun getWidth(): Int = (16 * scale).toInt()
        override fun getHeight(): Int = (16 * scale).toInt()

        override fun render(graphics: GuiGraphics) {
            val bounds = ScreenRectangle(0, 0, (16 * scale).toInt(), (16 * scale).toInt())
            //? > 1.21.5 {
            graphics.guiRenderState.submitPicturesInPictureState(
                SpinningItemRenderState(
                    item, xSpeed, ySpeed, zSpeed,
                    scale,
                    bounds,
                    graphics.scissorStack.peek(),
                    graphics.pose(),
                ),
            )
            //?}
        }

    }
}
