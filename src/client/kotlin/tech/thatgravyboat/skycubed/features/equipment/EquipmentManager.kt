package tech.thatgravyboat.skycubed.features.equipment

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.equipment.EquipmentAPI
import tech.thatgravyboat.skyblockapi.api.profile.equipment.EquipmentSlot
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.config.Config

object EquipmentManager {

    private val slotTexture = ResourceLocation.withDefaultNamespace("container/slot")
    private val necklaceTexture = SkyCubed.id("equipment/necklace")
    private val cloakTexture = SkyCubed.id("equipment/cloak")
    private val beltTexture = SkyCubed.id("equipment/belt")
    private val glovesTexture = SkyCubed.id("equipment/gloves")

    private var lastX: Int = 0
    private var lastY: Int = 0

    private val isEnabled: Boolean
        get() = Config.equipmentSlots && LocationAPI.isOnSkyblock

    fun onRenderScreen(screen: InventoryScreen, graphics: GuiGraphics, left: Int, top: Int, mouseX: Int, mouseY: Int) {
        if (!this.isEnabled) return
        replaceSlot(screen.menu)

        lastX = left
        lastY = top

        val leftPos = lastX + 76
        val topPos = lastY + 7

        EquipmentSlot.entries.forEachIndexed { index, slot ->
            val y = topPos + index * 18
            graphics.blitSprite(slotTexture, leftPos, y, 18, 18)
            val stack = EquipmentAPI.equipment[slot] ?: ItemStack.EMPTY
            if (mouseX in leftPos + 1..leftPos + 16 && mouseY in y + 1..y + 16) {
                graphics.fill(RenderType.guiOverlay(), leftPos + 1, y + 1, leftPos + 17, y + 17, -2130706433)
            }
            if (stack.isEmpty) {
                graphics.blitSprite(getEmptySlotTexture(slot), leftPos + 1, y + 1, 16, 16)
            } else {
                graphics.renderFakeItem(stack, leftPos + 1, y + 1)
            }
        }
    }

    @Subscription
    fun onRenderForeground(event: RenderScreenForegroundEvent) {
        if (!this.isEnabled) return
        if (event.screen !is InventoryScreen) return
        val x = lastX + 76
        val y = lastY + 7
        val (mouseX, mouseY) = McClient.mouse
        EquipmentSlot.entries.forEachIndexed { index, slot ->
            val slotY = y + index * 18
            if (mouseX.toInt() in x + 1..x + 16 && mouseY.toInt() in slotY + 1..slotY + 16) {
                val stack = EquipmentAPI.equipment[slot]?.takeIf { !it.isEmpty } ?: return
                event.graphics.renderTooltip(McClient.self.font, stack, mouseX.toInt(), mouseY.toInt())
            }
        }
    }

    @Subscription
    fun onMouseClick(event: ScreenMouseClickEvent) {
        if (!this.isEnabled) return
        if (event.screen !is InventoryScreen) return
        val x = lastX + 76
        val y = lastY + 7
        EquipmentSlot.entries.forEachIndexed { index, _ ->
            val slotY = y + index * 18
            if (event.x.toInt() in x + 1..x + 16 && event.y.toInt() in slotY + 1..slotY + 16) {
                McClient.self.connection?.sendCommand("equipment")
            }
        }
    }

    private fun getEmptySlotTexture(slot: EquipmentSlot): ResourceLocation = when (slot) {
        EquipmentSlot.NECKLACE -> necklaceTexture
        EquipmentSlot.CLOAK -> cloakTexture
        EquipmentSlot.BELT -> beltTexture
        EquipmentSlot.GLOVES -> glovesTexture
    }

    private fun replaceSlot(menu: AbstractContainerMenu) {
        if (menu.slots.size < 46) return
        if (menu.slots[45] is HiddenSlot) return
        menu.slots[45] = HiddenSlot(menu.slots[45])
    }

    private class HiddenSlot(val slot: Slot) : Slot(slot.container, slot.index, slot.x, slot.y) {
        override fun isActive(): Boolean = !isEnabled && slot.isActive
        override fun getNoItemIcon() = this.slot.noItemIcon
        override fun setByPlayer(itemStack: ItemStack, itemStack2: ItemStack) =
            this.slot.setByPlayer(itemStack, itemStack2)
    }
}