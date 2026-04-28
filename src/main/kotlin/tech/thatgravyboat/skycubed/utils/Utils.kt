package tech.thatgravyboat.skycubed.utils

import com.mojang.authlib.SignatureState
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.mojang.authlib.minecraft.MinecraftProfileTextures
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.resources.SkinManager
//? if >= 26.1 {
import net.minecraft.util.TriState
//? } else
//import com.teamresourceful.resourcefullib.common.utils.TriState
import net.minecraft.util.Util
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.PlayerSkin
import tech.thatgravyboat.skycubed.mixins.SkinManagerInvoker
import java.util.concurrent.CompletableFuture

object Utils {

    fun drawRpgPlayer(
        graphics: GuiGraphicsExtractor,
        entity: AbstractClientPlayer,
        x: Int, y: Int, width: Int, height: Int, scale: Float,
    ) {
        RpgPlayerRenderer.draw(graphics, entity, x, y, width, height, scale)
    }

    fun Screen.fullyRender(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) {
        //~ if >= 26.1 'render' -> 'extractRenderState'
        this.extractRenderStateWithTooltipAndSubtitles(graphics, mouseX, mouseY, partialTicks)
    }

    fun PlayerInfo.toSkin(): PlayerSkin = this.skin

    fun resetCursor() {
        // TODO: what
        //? if < 1.21.9
        /*com.teamresourceful.resourcefullib.client.utils.CursorUtils.setDefault()*/
    }

    fun tristate(boolean: Boolean?): TriState = when (boolean) {
        true -> TriState.TRUE
        false -> TriState.FALSE
        null -> /*? >= 26.1 {*/ TriState.DEFAULT /*?} else */// TriState.UNDEFINED
    }
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
