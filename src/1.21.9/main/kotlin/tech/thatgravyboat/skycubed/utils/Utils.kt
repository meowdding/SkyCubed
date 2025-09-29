package tech.thatgravyboat.skycubed.utils

import com.mojang.authlib.SignatureState
import com.mojang.authlib.minecraft.MinecraftProfileTexture
import com.mojang.authlib.minecraft.MinecraftProfileTextures
import net.minecraft.Util
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.resources.SkinManager
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.PlayerSkin
import tech.thatgravyboat.skycubed.mixins.SkinManagerInvoker
import java.util.concurrent.CompletableFuture


actual object Utils {

    actual fun drawRpgPlayer(
        graphics: GuiGraphics,
        entity: AbstractClientPlayer,
        width: Int, height: Int, scale: Float,
    ) {
        RpgPlayerRenderer.draw(graphics, entity, width, height, scale)
    }
}

actual fun SkinManager.getSkin(texture: String): CompletableFuture<PlayerSkin> {
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
