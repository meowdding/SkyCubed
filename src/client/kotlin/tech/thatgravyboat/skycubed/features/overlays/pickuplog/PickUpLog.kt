package tech.thatgravyboat.skycubed.features.overlays

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
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
import tech.thatgravyboat.skycubed.api.overlays.EditOverlaysScreen
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.api.overlays.OverlayScreen
import tech.thatgravyboat.skycubed.config.PickUpLogConfig
import tech.thatgravyboat.skycubed.features.overlays.pickuplog.PickUpLogItem
import tech.thatgravyboat.skycubed.utils.findWithIndex

object PickUpLog : Overlay {
    override val name = Text.of("Item Pick Up Log")
    override val position = PickUpLogConfig.position
    override val bounds
        get() = (if ((EditOverlaysScreen.inScreen() || OverlayScreen.inScreen()) && display == null) exampleDisplay else display)
            ?.let { it.getWidth() to it.getHeight() } ?: (0 to 0)

    private var display: Display? = null
    private val exampleDisplay by lazy {
        Displays.column(
            Displays.row(
                Displays.item(Items.EGG.defaultInstance, 10, 10),
                Displays.text(Text.of("Egg")),
                Displays.text(Text.of("+20").withColor(TextColor.GREEN)),
                spacing = 5,
            ),
            Displays.row(
                Displays.item(Items.EGG.defaultInstance, 10, 10),
                Displays.text(Text.of("Egg")),
                Displays.text(Text.of("-17").withColor(TextColor.RED)),
                spacing = 5,
            )
        )
    }

    private val addedItems = mutableListOf<PickUpLogItem>()
    private val removedItems = mutableListOf<PickUpLogItem>()
    private var lastInventory = mutableMapOf<Int, ItemStack>()

    private var lastWorldSwitchTime: Long? = null

    @Subscription
    fun onInvChange(event: PlayerInventoryChangeEvent) {
        if (event.slot == 8) return

        val newStack = event.item
        val oldStack = lastInventory[event.slot]
        val diff = newStack.count - (oldStack?.count ?: 0)

        if (diff != 0) {
            val (targetList, stackToUse) = if (diff > 0) {
                addedItems to newStack
            } else {
                removedItems to (oldStack ?: newStack)
            }

            val existingItem = targetList.findWithIndex { it.stack.isSameItem(stackToUse) }
            if (existingItem != null) {
                targetList[existingItem.index] = existingItem.value + diff
            } else {
                targetList.add(PickUpLogItem(stackToUse.copy(), diff, System.currentTimeMillis()))
            }

            addedItems.removeIf(PickUpLogItem::isEmpty)
            removedItems.removeIf(PickUpLogItem::isEmpty)
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
        val items = addedItems + removedItems
        if (items.isEmpty()) {
            display = null
            return
        }
        display = items.compact().map {
            PickUpLogConfig.appearance.map { component -> component.display(it) }.toRow(5)
        }.toColumn()
    }

    private fun List<PickUpLogItem>.compact() =
        takeIf { !PickUpLogConfig.compact } ?: groupBy { it.stack.item }.map { (_, items) ->
            items.reduce { acc, item -> acc.copy(difference = acc.difference + item.difference) }
        }.filter(PickUpLogItem::isNotEmpty)

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        if ((EditOverlaysScreen.inScreen() || OverlayScreen.inScreen()) && display == null) {
            exampleDisplay.render(graphics)
        } else display?.render(graphics)
    }

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        lastWorldSwitchTime = System.currentTimeMillis()
    }
}

