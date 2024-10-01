package tech.thatgravyboat.skycubed.api.displays

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import tech.thatgravyboat.skycubed.utils.font
import tech.thatgravyboat.skycubed.utils.pushPop

object Displays {

    fun empty(width: Int, height: Int): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {}
        }
    }

    fun supplied(display: () -> Display): Display {
        return object : Display {
            override fun getWidth() = display().getWidth()
            override fun getHeight() = display().getHeight()
            override fun render(graphics: GuiGraphics) {
                display().render(graphics)
            }
        }
    }

    fun background(sprite: ResourceLocation, display: Display): Display {
        return object : Display {
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                graphics.blitSprite(sprite, 0, 0, display.getWidth(), display.getHeight())
                display.render(graphics)
            }
        }
    }

    fun padding(padding: Int, display: Display): Display {
        return padding(padding, padding, display)
    }

    fun padding(padX: Int, padY: Int, display: Display): Display {
        return padding(padX, padX, padY, padY, display)
    }

    fun padding(left: Int, right: Int, top: Int, bottom: Int, display: Display): Display {
        return object : Display {
            override fun getWidth() = left + display.getWidth() + right
            override fun getHeight() = top + display.getHeight() + bottom
            override fun render(graphics: GuiGraphics) {
                display.render(graphics, left, top)
            }
        }
    }

    fun center(width: Int, height: Int, display: Display): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                display.render(graphics, (width - display.getWidth()) / 2, (height - display.getHeight()) / 2)
            }
        }
    }

    fun sprite(sprite: ResourceLocation, width: Int, height: Int): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                graphics.blitSprite(sprite, width, height, 0, 0, 0, 0, width, height)
            }
        }
    }

    fun text(text: String, color: () -> UInt = { 0xFFFFFFFFu }, shadow: Boolean = true): Display {
        return text({ text }, color, shadow)
    }

    fun text(text: () -> String, color: () -> UInt = { 0xFFFFFFFFu }, shadow: Boolean = true): Display {
        return object : Display {

            val component: MutableComponent
                get() = Text.of(text())

            override fun getWidth() = component.width
            override fun getHeight() = 10
            override fun render(graphics: GuiGraphics) {
                graphics.drawString(graphics.font, component, 0, 1, color().toInt(), shadow)
            }
        }
    }

    fun row(vararg displays: Display): Display {
        return object : Display {
            override fun getWidth() = displays.sumOf { it.getWidth() }
            override fun getHeight() = displays.maxOf { it.getHeight() }
            override fun render(graphics: GuiGraphics) {
                graphics.pushPop {
                    displays.forEach {
                        it.render(graphics)
                        translate(it.getWidth().toFloat(), 0f, 0f)
                    }
                }
            }
        }
    }

    fun column(vararg displays: Display): Display {
        return object : Display {
            override fun getWidth() = displays.maxOf { it.getWidth() }
            override fun getHeight() = displays.sumOf { it.getHeight() }
            override fun render(graphics: GuiGraphics) {
                graphics.pushPop {
                    displays.forEach {
                        it.render(graphics)
                        translate(0f, it.getHeight().toFloat(), 0f)
                    }
                }
            }
        }
    }
}