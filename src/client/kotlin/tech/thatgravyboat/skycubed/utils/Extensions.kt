package tech.thatgravyboat.skycubed.utils

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

internal val GuiGraphics.font get() = Minecraft.getInstance().font

internal inline fun GuiGraphics.pushPop(crossinline block: PoseStack.() -> Unit) {
    this.pose().pushPose()
    this.pose().block()
    this.pose().popPose()
}

internal fun GuiGraphics.blitSpritePercentX(id: ResourceLocation, x: Int, y: Int, width: Int, height: Int, percent: Float) {
    this.blitSprite(id, width, height, 0, 0, x, y, (width * percent).toInt(), height)
}

internal fun GuiGraphics.blitSpritePercentY(id: ResourceLocation, x: Int, y: Int, width: Int, height: Int, percent: Float) {
    this.blitSprite(id, width, height, 0, 0, x, y, width, (height * percent).toInt())
}

internal fun GuiGraphics.drawScaledString(text: String, x: Int, y: Int, width: Int, color: Int, shadow: Boolean = true) {
    pushPop {
        val textWidth = font.width(text)
        val scale = (width.toFloat() / textWidth)
        translate(x.toFloat(), y.toFloat(), 0f)
        scale(scale.coerceAtMost(1f), scale.coerceAtMost(1f), 1f)
        drawString(font, text, if (scale > 1f) (width - textWidth) / 2 else 0, 0, color, shadow)
    }
}

internal fun String?.capitalize(): String {
    if (this == null) return ""
    return this.replace("_", " ")
        .lowercase()
        .replaceFirstChar(Char::titlecase)
}

internal fun Int.toOrdinal(): String {
    val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when {
        this in 11..13 -> "${this}th"
        else -> "${this}${suffixes[this % 10]}"
    }
}