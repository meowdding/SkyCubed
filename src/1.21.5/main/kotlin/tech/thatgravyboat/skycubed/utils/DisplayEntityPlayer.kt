package tech.thatgravyboat.skycubed.utils

import com.mojang.authlib.GameProfile
import net.minecraft.Util
import net.minecraft.client.player.RemotePlayer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.scores.PlayerTeam
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.PlayerSkin
import java.util.concurrent.CompletableFuture

class DisplayEntityPlayer(
    skin: CompletableFuture<PlayerSkin>,
    var isTransparent: Boolean = false,
    var armor: List<ItemStack>,
) : RemotePlayer(
    McClient.self.level,
    GameProfile(Util.NIL_UUID, "Display")
) {

    private val skin: CompletableFuture<PlayerSkin> = skin.thenApply { PlayerSkin(it.texture(), it.textureUrl(), null, null, it.model(), it.secure()) }
    private val hasNoArmor: Boolean = armor.all(ItemStack::isEmpty)
    override fun getItemBySlot(slot: EquipmentSlot): ItemStack = when (slot) {
        EquipmentSlot.HEAD -> armor.getOrNull(0) ?: ItemStack.EMPTY
        EquipmentSlot.CHEST -> armor.getOrNull(1) ?: ItemStack.EMPTY
        EquipmentSlot.LEGS -> armor.getOrNull(2) ?: ItemStack.EMPTY
        EquipmentSlot.FEET -> armor.getOrNull(3) ?: ItemStack.EMPTY
        else -> ItemStack.EMPTY
    }

    override fun getMainArm(): HumanoidArm = HumanoidArm.RIGHT

    override fun isSpectator() = false
    override fun isCreative() = false

    override fun isInvisible() = this.isTransparent
    override fun isInvisibleTo(player: Player) = this.isTransparent && !hasNoArmor

    override fun getTeam() = object : PlayerTeam(null, "display") {
        override fun getNameTagVisibility() = Visibility.NEVER
    }
}

interface DisplayEntityPlayerRenderStateExtension {

    fun `skycubed$setIsDisplayEntityPlayer`(value: Boolean)

    fun `skycubed$isDisplayEntityPlayer`(): Boolean

    companion object {

        @JvmField
        val CHANGE_INVISIBLE_RENDERER = ThreadLocal<Boolean>()
    }
}
