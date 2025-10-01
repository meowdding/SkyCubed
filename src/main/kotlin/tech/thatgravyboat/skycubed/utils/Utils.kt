package tech.thatgravyboat.skycubed.utils

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.msrandom.stub.Stub
import tech.thatgravyboat.skyblockapi.platform.PlayerSkin
import java.util.concurrent.CompletableFuture

@Stub
expect object Utils {

    fun drawRpgPlayer(
        graphics: GuiGraphics,
        entity: AbstractClientPlayer,
        width: Int, height: Int, scale: Float,
    )

    fun Screen.fullyRender(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float)

    fun PlayerInfo.toSkin(): PlayerSkin

    fun resetCursor()
}

expect fun DisplayEntityPlayer(
    skin: CompletableFuture<PlayerSkin>,
    armor: List<ItemStack>,
    isTransparent: Boolean = false,
): LivingEntity
