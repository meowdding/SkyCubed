package tech.thatgravyboat.skycubed.utils

import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class HiddenSlot(val slot: Slot, val isEnabled: () -> Boolean = { false }) : Slot(slot.container, slot.index, slot.x, slot.y) {
    override fun isActive(): Boolean = isEnabled() && slot.isActive
    override fun getNoItemIcon() = this.slot.noItemIcon
    override fun setByPlayer(itemStack: ItemStack, itemStack2: ItemStack) = this.slot.setByPlayer(itemStack, itemStack2)
}