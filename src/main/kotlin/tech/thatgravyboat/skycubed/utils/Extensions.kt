package tech.thatgravyboat.skycubed.utils

import com.mojang.authlib.SignatureState
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.mojang.authlib.minecraft.MinecraftProfileTextures
import com.mojang.blaze3d.platform.InputConstants
import earth.terrarium.olympus.client.pipelines.RoundedRectangle
import net.minecraft.Util
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.client.resources.SkinManager
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.*
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.json.Json
import tech.thatgravyboat.skycubed.mixins.SkinManagerInvoker
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.DurationUnit

val commentRegex = Regex("(^\\s*//.*$)|(/\\*(\\*(?!/)|[^*])*\\*/)", RegexOption.MULTILINE)

inline fun <reified T : Any> InputStream.readJsonc(): T =
    Json.gson.fromJson(bufferedReader().readText().replace(commentRegex, ""), typeOf<T>().javaType)

internal fun GuiGraphics.blitSpritePercentX(id: ResourceLocation, x: Int, y: Int, width: Int, height: Int, percent: Float) {
    this.scissor(x, y, (width * percent).toInt(), height) {
        this.drawSprite(id, x, y, width, height)
    }
}

internal fun GuiGraphics.blitSpritePercentY(id: ResourceLocation, x: Int, y: Int, width: Int, height: Int, percent: Float) {
    this.scissor(x, y, width, (height * percent).toInt()) {
        this.drawSprite(id, x, y, width, height)
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
    borderSize: Int = 0, radius: Int = 0
) {
    // TODO needs to be fixed for 1.21.5
    RoundedRectangle.draw(
        this, x, y, width, height,
        backgroundColor, borderColor, width.coerceAtMost(height) * (radius / 100f), borderSize
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
        player
    )
}

val CompletableFuture<*>.isActuallyDone: Boolean get() {
    return this.isDone && !this.isCompletedExceptionally && !this.isCancelled
}

fun SkinManager.getSkin(texture: String): CompletableFuture<PlayerSkin> {
    val result = runCatching {
        val manager = McClient.self.skinManager as SkinManagerInvoker
        manager.callRegisterTextures(
            Util.NIL_UUID,
            MinecraftProfileTextures(
                MinecraftProfileTexture(texture, emptyMap()),
                null,
                null,
                SignatureState.SIGNED,
            ),
        )
    }

    return result.getOrNull() ?: CompletableFuture.failedFuture(result.exceptionOrNull()!!)
}
