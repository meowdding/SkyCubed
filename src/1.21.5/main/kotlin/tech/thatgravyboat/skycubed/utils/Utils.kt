package tech.thatgravyboat.skycubed.utils

import com.teamresourceful.resourcefullib.client.utils.CursorUtils
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.platform.PlayerSkin
import java.util.concurrent.CompletableFuture


actual object Utils {

    actual fun drawRpgPlayer(
        graphics: GuiGraphics,
        entity: AbstractClientPlayer,
        width: Int, height: Int, scale: Float,
    ) {
    }

    actual fun Screen.fullyRender(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderWithTooltip(graphics, mouseX, mouseY, partialTicks)
    }

    actual fun PlayerInfo.toSkin(): PlayerSkin = this.skin

    actual fun resetCursor() {
        CursorUtils.setDefault()
    }
}

actual fun DisplayEntityPlayer(
    skin: CompletableFuture<PlayerSkin>,
    armor: List<ItemStack>,
    isTransparent: Boolean,
): LivingEntity = DisplayEntityPlayer(skin, isTransparent, armor)
