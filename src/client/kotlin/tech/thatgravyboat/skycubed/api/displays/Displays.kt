package tech.thatgravyboat.skycubed.api.displays

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import tech.thatgravyboat.skycubed.utils.fillRect
import tech.thatgravyboat.skycubed.utils.pushPop

private const val NO_SPLIT = -1

object Displays {

    fun empty(width: Int = 0, height: Int = 0): Display {
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

    fun background(color: UInt, radius: Float, display: Display): Display {
        return object : Display {
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                RenderSystem.enableBlend()
                graphics.fillRect(0, 0, getWidth(), getHeight(), color.toInt(), radius = radius.toInt())
                display.render(graphics)
                RenderSystem.disableBlend()
            }
        }
    }

    fun background(sprite: ResourceLocation, display: Display): Display {
        return object : Display {
            override fun getWidth() = display.getWidth()
            override fun getHeight() = display.getHeight()
            override fun render(graphics: GuiGraphics) {
                graphics.blitSprite(RenderType::guiTextured, sprite, 0, 0, display.getWidth(), display.getHeight())
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

    fun center(width: Int = -1, height: Int = -1, display: Display): Display {
        return object : Display {
            override fun getWidth() = if (width == -1) display.getWidth() else width
            override fun getHeight() = if (height == -1) display.getHeight() else height
            override fun render(graphics: GuiGraphics) {
                display.render(graphics, (getWidth() - display.getWidth()) / 2, (getHeight() - display.getHeight()) / 2)
            }
        }
    }

    fun outline(color: () -> UInt, display: Display): Display {
        return object : Display {
            override fun getWidth() = display.getWidth() + 2
            override fun getHeight() = display.getHeight() + 2
            override fun render(graphics: GuiGraphics) {
                display.render(graphics, 1, 1)
                graphics.renderOutline(0, 0, getWidth(), getHeight(), color().toInt())
            }
        }
    }

    fun face(texture: () -> ResourceLocation, size: Int = 8): Display {
        return object : Display {
            override fun getWidth(): Int = size
            override fun getHeight(): Int = size

            override fun render(graphics: GuiGraphics) {
                PlayerFaceRenderer.draw(graphics, texture(), 0, 0, 8, true, false, -1)
            }
        }
    }

    fun sprite(sprite: ResourceLocation, width: Int, height: Int): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                graphics.blitSprite(RenderType::guiTextured, sprite, width, height, 0, 0, 0, 0, width, height)
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
                graphics.drawString(McFont.self, component, 0, 1, color().toInt(), shadow)
            }
        }
    }

    fun text(
        component: Component,
        maxWidth: Int = NO_SPLIT,
        color: () -> UInt = { 0xFFFFFFFFu },
        shadow: Boolean = true
    ): Display {
        val font = McClient.self.font
        val lines = if (maxWidth == NO_SPLIT) listOf(component.visualOrderText) else font.split(component, maxWidth)
        val width = lines.maxOfOrNull { font.width(it) } ?: 0
        val height = lines.size * font.lineHeight

        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                lines.forEachIndexed { index, line ->
                    graphics.drawString(font, line, 0, index * font.lineHeight, color().toInt(), shadow)
                }
            }
        }
    }

    fun text(
        sequence: FormattedCharSequence,
        color: () -> UInt = { 0xFFFFFFFFu },
        shadow: Boolean = true
    ): Display {
        val font = McFont.self
        val width = font.width(sequence)
        val height = font.lineHeight

        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                graphics.drawString(font, sequence, 0, 1, color().toInt(), shadow)
            }
        }
    }

    fun row(vararg displays: Display, spacing: Int = 0): Display {
        return object : Display {
            override fun getWidth() = displays.sumOf { it.getWidth() } + spacing * (displays.size - 1)
            override fun getHeight() = displays.maxOfOrNull { it.getHeight() } ?: 0
            override fun render(graphics: GuiGraphics) {
                graphics.pushPop {
                    displays.forEachIndexed { index, display ->
                        display.render(graphics)
                        if (index < displays.size - 1) {
                            translate((display.getWidth() + spacing).toFloat(), 0f, 0f)
                        } else {
                            translate(display.getWidth().toFloat(), 0f, 0f)
                        }
                    }
                }
            }
        }
    }

    fun column(vararg displays: Display, spacing: Int = 0): Display {
        return object : Display {
            override fun getWidth() = displays.maxOfOrNull { it.getWidth() } ?: 0
            override fun getHeight() = displays.sumOf { it.getHeight() } + spacing * (displays.size - 1)
            override fun render(graphics: GuiGraphics) {
                graphics.pushPop {
                    displays.forEachIndexed { index, display ->
                        display.render(graphics)
                        if (index < displays.size - 1) {
                            translate(0f, (display.getHeight() + spacing).toFloat(), 0f)
                        } else {
                            translate(0f, display.getHeight().toFloat(), 0f)
                        }
                    }
                }
            }
        }
    }

    fun item(item: ItemStack, width: Int = 16, height: Int = 16): Display {
        return object : Display {
            override fun getWidth() = width
            override fun getHeight() = height
            override fun render(graphics: GuiGraphics) {
                graphics.pushPop {
                    scale(width / 16f, height / 16f, 1f)
                    graphics.renderItem(item, 0, 0)
                }
            }
        }
    }

    fun <T> renderable(renderable: T, width: Int = -1, height: Int = -1): Display
        where T: Renderable, T: LayoutElement {
        return object : Display {
            override fun getWidth(): Int = if (width == -1) renderable.width else width
            override fun getHeight(): Int = if (height == -1) renderable.height else height
            override fun render(graphics: GuiGraphics) {
                renderable.render(graphics, -1, -1, 0f)
            }
        }
    }
}