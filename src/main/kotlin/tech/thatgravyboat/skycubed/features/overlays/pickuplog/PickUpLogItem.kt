package tech.thatgravyboat.skycubed.features.overlays.pickuplog

import net.minecraft.world.item.ItemStack

data class PickUpLogItem(
    val stack: ItemStack,
    val difference: Int,
    var time: Long,
) {

    fun isEmpty(): Boolean = this.difference == 0
    fun isNotEmpty(): Boolean = !isEmpty()

    operator fun plus(addition: Int): PickUpLogItem {
        return this.copy(
            difference = this.difference + addition,
            time = System.currentTimeMillis()
        )
    }
}
