package tech.thatgravyboat.skycubed.utils

import com.mojang.authlib.GameProfile
import net.minecraft.Util
import net.minecraft.client.player.RemotePlayer
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.PlayerModelPart
import net.minecraft.world.item.ItemStack
import net.minecraft.world.scores.PlayerTeam
import tech.thatgravyboat.skyblockapi.helpers.McClient

class DisplayEntityPlayer(
    skin: PlayerSkin?,
    armor: List<ItemStack>,
    private val invisible: Boolean = false,
) : RemotePlayer(
    McClient.self.level,
    GameProfile(Util.NIL_UUID, "Display")
) {

    private val skin = skin?.let { PlayerSkin(it.texture, it.textureUrl, null, null, it.model, it.secure) }
    private val hasNoArmor: Boolean = armor.all(ItemStack::isEmpty)

    init {
        for (i in 0 until 4) {
            inventory.armor[i] = armor.reversed()[i]
        }
    }

    override fun getSkin(): PlayerSkin = this.skin ?: super.getSkin()

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