package tech.thatgravyboat.skycubed.utils

import com.mojang.authlib.GameProfile
import net.minecraft.Util
import net.minecraft.client.player.RemotePlayer
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.PlayerModelPart
import net.minecraft.world.item.ItemStack
import net.minecraft.world.scores.PlayerTeam
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.concurrent.CompletableFuture

class DisplayEntityPlayer(
    skin: CompletableFuture<PlayerSkin>,
    armor: List<ItemStack>,
    private val invisible: Boolean = false,
) : RemotePlayer(
    McClient.self.level,
    GameProfile(Util.NIL_UUID, "Display")
) {

    private val skin: CompletableFuture<PlayerSkin> = skin.thenApply { PlayerSkin(it.texture, it.textureUrl, null, null, it.model, it.secure) }
    private val hasNoArmor: Boolean = armor.all(ItemStack::isEmpty)

    init {
        equipment.set(EquipmentSlot.HEAD, armor.getOrNull(0) ?: ItemStack.EMPTY)
        equipment.set(EquipmentSlot.CHEST, armor.getOrNull(1) ?: ItemStack.EMPTY)
        equipment.set(EquipmentSlot.LEGS, armor.getOrNull(2) ?: ItemStack.EMPTY)
        equipment.set(EquipmentSlot.FEET, armor.getOrNull(3) ?: ItemStack.EMPTY)
    }

    override fun getSkin(): PlayerSkin = if (skin.isActuallyDone) skin.get() else super.getSkin()

    override fun isSpectator() = false
    override fun isCreative() = false

    override fun isInvisible() = this.invisible
    override fun isInvisibleTo(player: Player) = this.invisible && !hasNoArmor

    override fun getTeam() = object : PlayerTeam(null, "display") {
        override fun getNameTagVisibility() = Visibility.NEVER
    }

    override fun isModelPartShown(part: PlayerModelPart) = true
}

interface DisplayEntityPlayerRenderStateExtension {

    fun `skycubed$setIsDisplayEntityPlayer`(value: Boolean)

    fun `skycubed$isDisplayEntityPlayer`(): Boolean

    companion object {

        @JvmField
        val CHANGE_INVISIBLE_RENDERER = ThreadLocal<Boolean>()
    }
}
