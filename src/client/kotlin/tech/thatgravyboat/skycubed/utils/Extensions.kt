package tech.thatgravyboat.skycubed.utils

import earth.terrarium.olympus.client.shader.builtin.RoundedRectShader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.helpers.McFont

internal fun GuiGraphics.blitSpritePercentX(id: ResourceLocation, x: Int, y: Int, width: Int, height: Int, percent: Float) {
    this.blitSprite(RenderType::guiTextured, id, width, height, 0, 0, x, y, (width * percent).toInt(), height)
}

internal fun GuiGraphics.blitSpritePercentY(id: ResourceLocation, x: Int, y: Int, width: Int, height: Int, percent: Float) {
    this.blitSprite(RenderType::guiTextured, id, width, height, 0, 0, x, y, width, (height * percent).toInt())
}

internal fun GuiGraphics.drawScaledString(text: String, x: Int, y: Int, width: Int, color: Int, shadow: Boolean = true) {
    pushPop {
        val textWidth = McFont.width(text)
        val scale = (width.toFloat() / textWidth)
        translate(x.toFloat(), y.toFloat(), 0f)
        scale(scale.coerceAtMost(1f), scale.coerceAtMost(1f), 1f)
        drawString(McFont.self, text, if (scale > 1f) (width - textWidth) / 2 else 0, 0, color, shadow)
    }
}

internal fun GuiGraphics.fillRect(
    x: Int, y: Int, width: Int, height: Int,
    backgroundColor: Int, borderColor: Int = 0x00000000,
    borderSize: Int = 0, radius: Int = 0
) {
    val xOffset = this.pose().last().pose().m30()
    val yOffset = this.pose().last().pose().m31()
    pushPop {
        translate(-xOffset, -yOffset, 0f)
        RoundedRectShader.fill(
            this@fillRect, (x + xOffset).toInt(), (y + yOffset).toInt(), width, height,
            backgroundColor, borderColor, radius.toFloat(), borderSize
        )
    }
}

internal fun String.capitalize() =
    replace("_", " ").lowercase().split(" ").joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

internal fun Int.toOrdinal(): String {
    val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when {
        this in 11..13 -> "${this}th"
        else -> "${this}${suffixes[this % 10]}"
    }
}