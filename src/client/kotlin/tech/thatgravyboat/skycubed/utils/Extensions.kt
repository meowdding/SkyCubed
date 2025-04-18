package tech.thatgravyboat.skycubed.utils

import com.mojang.blaze3d.platform.InputConstants
import earth.terrarium.olympus.client.pipelines.RoundedRectanage
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skyblockapi.utils.json.Json
import java.io.InputStream
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.DurationUnit

val commentRegex = Regex("(^\\s*//.*$)|(/\\*(\\*(?!/)|[^*])*\\*/)", RegexOption.MULTILINE)

inline fun <reified T : Any> InputStream.readJsonc(): T =
    Json.gson.fromJson(bufferedReader().readText().replace(commentRegex, ""), typeOf<T>().javaType)

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
    backgroundColor: Int, borderColor: Int = 0x0,
    borderSize: Int = 0, radius: Int = 0
) {
    val xOffset = this.pose().last().pose().m30()
    val yOffset = this.pose().last().pose().m31()
    pushPop {
        translate(-xOffset, -yOffset, 0f)
        RoundedRectanage.draw(
            this@fillRect, (x + xOffset).toInt(), (y + yOffset).toInt(), width, height,
            backgroundColor, borderColor, width.coerceAtMost(height) * (radius / 100f), borderSize
        )
    }
}

internal fun Int.toOrdinal(): String {
    val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when {
        this in 11..13 -> "${this}th"
        else -> "${this}${suffixes[this % 10]}"
    }
}

// Taken from https://github.com/j10a1n15/CustomScoreboard/
internal fun Duration.formatReadableTime(biggestUnit: DurationUnit, maxUnits: Int = 1): String {
    val units = listOf(
        DurationUnit.DAYS to this.inWholeDays,
        DurationUnit.HOURS to this.inWholeHours % 24,
        DurationUnit.MINUTES to this.inWholeMinutes % 60,
        DurationUnit.SECONDS to this.inWholeSeconds % 60,
        DurationUnit.MILLISECONDS to this.inWholeMilliseconds % 1000,
    )

    val unitNames = mapOf(
        DurationUnit.DAYS to "d",
        DurationUnit.HOURS to "h",
        DurationUnit.MINUTES to "min",
        DurationUnit.SECONDS to "s",
        DurationUnit.MILLISECONDS to "ms",
    )

    val filteredUnits = units.dropWhile { it.first != biggestUnit }
        .filter { it.second > 0 }
        .take(maxUnits)

    return filteredUnits.joinToString(", ") { (unit, value) ->
        "$value${unitNames[unit]}"
    }.ifEmpty { "0 seconds" }
}

fun AbstractContainerMenu.click(slot: Slot) {
    val player = McPlayer.self ?: return
    McClient.self.gameMode?.handleInventoryMouseClick(
        this.containerId,
        slot.index,
        InputConstants.MOUSE_BUTTON_LEFT,
        ClickType.PICKUP,
        player
    )
}
