package tech.thatgravyboat.skycubed.utils

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.lib.platform.drawRoundedRectangle
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenDirection
import net.minecraft.client.resources.SkinManager
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.*
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.json.Json
import tech.thatgravyboat.skycubed.SkyCubed
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.DurationUnit

val commentRegex = Regex("(^\\s*//.*$)|(/\\*(\\*(?!/)|[^*])*\\*/)", RegexOption.MULTILINE)

inline fun <reified T : Any> InputStream.readJsonc(): T =
    Json.gson.fromJson(bufferedReader().readText().replace(commentRegex, ""), typeOf<T>().javaType)

fun ItemStack.getTooltipLines(): List<Component> = getTooltipLines(
    Item.TooltipContext.of(McLevel.self),
    McPlayer.self!!,
    if (McClient.options.advancedItemTooltips) TooltipFlag.ADVANCED else TooltipFlag.NORMAL,
)

internal fun GuiGraphics.blitSpritePercent(
    id: ResourceLocation,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    percent: Float,
    direction: ScreenDirection = ScreenDirection.RIGHT,
) {
    when (direction) {
        ScreenDirection.LEFT, ScreenDirection.RIGHT -> this.blitSpritePercentX(id, x, y, width, height, percent, direction)
        ScreenDirection.UP, ScreenDirection.DOWN -> this.blitSpritePercentY(id, x, y, width, height, percent, direction)
        else -> throw IllegalArgumentException("Direction must be WEST, EAST, UP or DOWN")
    }
}

internal fun GuiGraphics.blitSpritePercentX(
    id: ResourceLocation,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    percent: Float,
    direction: ScreenDirection = ScreenDirection.RIGHT,
) {
    when (direction) {
        ScreenDirection.LEFT -> this.scissor(x + width - (width * percent).toInt(), y, (width * percent).toInt(), height) {
            this.drawSprite(id, x, y, width, height)
        }

        ScreenDirection.RIGHT -> this.scissor(x, y, (width * percent).toInt(), height) {
            this.drawSprite(id, x, y, width, height)
        }

        else -> SkyCubed.error("Direction must be LEFT or RIGHT")
    }
}

internal fun GuiGraphics.blitSpritePercentY(
    id: ResourceLocation,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    percent: Float,
    direction: ScreenDirection = ScreenDirection.DOWN,
) {
    when (direction) {
        ScreenDirection.UP -> this.scissor(x, y + height - (height * percent).toInt(), width, (height * percent).toInt()) {
            this.drawSprite(id, x, y, width, height)
        }

        ScreenDirection.DOWN -> this.scissor(x, y, width, (height * percent).toInt()) {
            this.drawSprite(id, x, y, width, height)
        }

        else -> SkyCubed.error("Direction must be UP or DOWN")
    }
}

internal fun GuiGraphics.drawScaledString(text: String, x: Int, y: Int, width: Int, color: Int, shadow: Boolean = true) {
    this.pushPop {
        val textWidth = McFont.width(text)
        val scale = (width.toFloat() / textWidth)
        this.translate(x.toFloat(), y.toFloat())
        this.scale(scale.coerceAtMost(1f), scale.coerceAtMost(1f))
        this.drawString(text, if (scale > 1f) (width - textWidth) / 2 else 0, 0, color, shadow)
    }
}

internal fun GuiGraphics.fillRect(
    x: Int, y: Int, width: Int, height: Int,
    backgroundColor: Int, borderColor: Int = 0x0,
    borderSize: Int = 0, radius: Int = 0,
) {
    this.drawRoundedRectangle(
        x, y, width, height,
        backgroundColor.toUInt(), borderColor.toUInt(),
        width.coerceAtMost(height) * (radius / 100f), borderSize,
    )
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
        player,
    )
}

val CompletableFuture<*>.isActuallyDone: Boolean
    get() {
        return this.isDone && !this.isCompletedExceptionally && !this.isCancelled
    }

expect fun SkinManager.getSkin(texture: String): CompletableFuture<PlayerSkin>

fun <T : Enum<T>> T.next(): T {
    val constants = if (this.javaClass.isEnum) this.javaClass.enumConstants else this.javaClass.superclass.enumConstants
    return constants[(this.ordinal + 1) % constants.size] as T
}
