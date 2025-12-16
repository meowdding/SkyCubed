package tech.thatgravyboat.skycubed.utils

import com.mojang.authlib.SignatureState
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.mojang.authlib.minecraft.MinecraftProfileTextures
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.resources.SkinManager
import net.minecraft.util.Util
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.PlayerSkin
import tech.thatgravyboat.skycubed.mixins.SkinManagerInvoker
import java.util.concurrent.CompletableFuture

object Utils {

    fun drawRpgPlayer(
        graphics: GuiGraphics,
        entity: AbstractClientPlayer,
        x: Int, y: Int, width: Int, height: Int, scale: Float,
    ) {
        //? if > 1.21.5
        RpgPlayerRenderer.draw(graphics, entity, x, y, width, height, scale)
    }

    fun Screen.fullyRender(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this./*? if > 1.21.8 {*/renderWithTooltipAndSubtitles/*?} else {*//*renderWithTooltip*//*?}*/(graphics, mouseX, mouseY, partialTicks)
    }

    fun PlayerInfo.toSkin(): PlayerSkin = this.skin

    fun resetCursor() {
        //? if < 1.21.9
        /*com.teamresourceful.resourcefullib.client.utils.CursorUtils.setDefault()*/
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
