package tech.thatgravyboat.skycubed.features.overlays

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerInventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.isSameItem
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.api.displays.Display
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.api.displays.toColumn
import tech.thatgravyboat.skycubed.api.displays.toRow
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.PickUpLogConfig

object PickUpLog : Overlay {
    override val name = Text.of("Item Pick Up Log")
    override val position = PickUpLogConfig.position
    override val bounds get() = display?.let { it.getWidth() to it.getHeight() } ?: (0 to 0)

    private var display: Display? = null

    private val addedItems = mutableListOf<PickUpLogItem>()
    private val removedItems = mutableListOf<PickUpLogItem>()
    private var lastInventory = mutableMapOf<Int, ItemStack>()

    private var lastWorldSwitchTime: Long? = null

    @Subscription
    fun onInvChange(event: PlayerInventoryChangeEvent) {
        if (event.slot == 8) return

        val newStack = event.item
        val oldStack = lastInventory[event.slot]

        val newCount = newStack.count
        val oldCount = oldStack?.count ?: 0
        val diff = newCount - oldCount

        if (diff != 0) {
            val (targetList, stackToUse) = if (diff > 0) {
                addedItems to newStack
            } else {
                removedItems to (oldStack ?: newStack)
            }

            val existingItem = targetList.find { it.stack.isSameItem(stackToUse) }
            if (existingItem != null) {
                targetList[targetList.indexOf(existingItem)] = existingItem.copy(
                    difference = existingItem.difference + diff,
                    time = System.currentTimeMillis()
                )
            } else {
                targetList.add(PickUpLogItem(stackToUse.copy(), diff, System.currentTimeMillis()))
            }

            addedItems.removeIf { it.difference == 0 }
            removedItems.removeIf { it.difference == 0 }
            updateDisplay()
        }

        lastInventory[event.slot] = newStack.copy()
    }

    @Subscription
    @TimePassed("1s")
    fun onTick(event: TickEvent) {
        val currentTime = System.currentTimeMillis()
        addedItems.removeIf { it.time + 5000 < currentTime }
        removedItems.removeIf { it.time + 5000 < currentTime }
        updateDisplay()
    }

    private fun updateDisplay() {
        if (System.currentTimeMillis() - (lastWorldSwitchTime ?: 0) < 5000) {
            display = null
            return
        }
        display = listOf(addedItems, removedItems).flatten().compact().map {
            PickUpLogConfig.appearance.map { component -> component.display(it) }.toRow(5)
        }.toColumn()
    }

    private fun List<PickUpLogItem>.compact() =
        takeIf { !PickUpLogConfig.compact } ?: groupBy { it.stack.item }.map { (_, items) ->
            items.reduce { acc, item -> acc.copy(difference = acc.difference + item.difference) }
        }.filter { it.difference != 0 }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        display?.render(graphics)
    }

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        lastWorldSwitchTime = System.currentTimeMillis()
    }
}

enum class PickUpLogComponents(val display: (PickUpLogItem) -> Display) {
    ITEM_STACK({ Displays.item(it.stack) }),
    DIFFERENCE({
        if (it.difference < 0) Displays.text(Text.of(it.difference.toString()).withColor(TextColor.RED))
        else Displays.text(Text.of("+${it.difference}").withColor(TextColor.GREEN))
    }),
    NAME({ Displays.text(it.stack.hoverName) }),
    ;

    private val formattedName = name.split("_").joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }

    override fun toString() = formattedName

}

data class PickUpLogItem(
    val stack: ItemStack,
    val difference: Int,
    val time: Long,
)
