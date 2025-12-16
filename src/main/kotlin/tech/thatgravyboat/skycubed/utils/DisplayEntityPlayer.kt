package tech.thatgravyboat.skycubed.utils

import com.mojang.authlib.GameProfile
import net.minecraft.util.Util
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.player.Player
import tech.thatgravyboat.skyblockapi.platform.PlayerSkin
import net.minecraft.world.item.ItemStack
import net.minecraft.world.scores.PlayerTeam
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.concurrent.CompletableFuture

//? if > 1.21.8 {
import net.minecraft.client.entity.ClientMannequin
import tech.thatgravyboat.skyblockapi.platform.toResolvableProfile
import net.minecraft.world.item.component.ResolvableProfile
//?} else {
/*import net.minecraft.client.player.RemotePlayer as ClientMannequin
import net.minecraft.world.entity.player.PlayerModelPart
*///?}


class DisplayEntityPlayer(
    skin: CompletableFuture<PlayerSkin>,
    var armor: List<ItemStack>,
    var isTransparent: Boolean = false,
) : ClientMannequin(
    McClient.self.level,
    //? if > 1.21.8 {
    McClient.self.playerSkinRenderCache(),
    //?} else
    /*GameProfile(Util.NIL_UUID, "Display"),*/
) {

    //? if > 1.21.8 {
    private val skin: CompletableFuture<PlayerSkin> = skin.thenApply { PlayerSkin(it.body(), null, null, it.model(), it.secure()) }
    private val _profile = GameProfile(Util.NIL_UUID, "Display").toResolvableProfile()
    override fun getProfile(): ResolvableProfile {
        return _profile
    }
    //?} else {
    /*private val skin: CompletableFuture<PlayerSkin> = skin.thenApply { PlayerSkin(it.texture(), it.textureUrl(), null, null, it.model(), it.secure()) }

    override fun isSpectator() = false
    override fun isCreative() = false

    override fun isModelPartShown(part: PlayerModelPart): Boolean = true
    *///?}

    private val hasNoArmor: Boolean = armor.all(ItemStack::isEmpty)

    override fun getItemBySlot(slot: EquipmentSlot): ItemStack = when (slot) {
        EquipmentSlot.HEAD -> armor.getOrNull(0) ?: ItemStack.EMPTY
        EquipmentSlot.CHEST -> armor.getOrNull(1) ?: ItemStack.EMPTY
        EquipmentSlot.LEGS -> armor.getOrNull(2) ?: ItemStack.EMPTY
        EquipmentSlot.FEET -> armor.getOrNull(3) ?: ItemStack.EMPTY
        else -> ItemStack.EMPTY
    }

    override fun getMainArm(): HumanoidArm = HumanoidArm.RIGHT
    override fun getSkin(): PlayerSkin = if (skin.isActuallyDone) skin.get() else super.getSkin()

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
        val CHANGE_INVISIBLE_RENDERER: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
    }
}
