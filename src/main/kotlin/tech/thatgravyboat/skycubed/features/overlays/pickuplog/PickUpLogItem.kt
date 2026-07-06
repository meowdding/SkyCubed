package tech.thatgravyboat.skycubed.features.overlays.pickuplog

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockEnchantmentsRepo
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

data class PickUpLogItem(
    val stack: ItemStack,
    val difference: Int,
    var time: Long,
) {

    val hoverName: Component = run {
        val id = stack.getSkyBlockId()
        if (id == null || !id.isEnchantment) stack.hoverName
        else {
            val level = id.cleanId.substringAfter(SkyBlockId.DELIMITER).toIntValue()
            val enchants = SkyBlockEnchantmentsRepo.get(id.cleanId.substringBefore(SkyBlockId.DELIMITER))
            Text.of("${enchants?.name ?: "Unknown Enchantment"} $level") {
                color = if (enchants?.isUltimate == true) TextColor.PINK else stack.getData(DataTypes.RARITY)?.color ?: TextColor.WHITE
                bold = enchants?.isUltimate == true
            }
        }
    }

    fun isEmpty(): Boolean = this.difference == 0
    fun isNotEmpty(): Boolean = !isEmpty()

    operator fun plus(addition: Int): PickUpLogItem {
        return this.copy(
            difference = this.difference + addition,
            time = System.currentTimeMillis()
        )
    }
}
