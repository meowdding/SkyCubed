package tech.thatgravyboat.skycubed.features.overlays.pickuplog

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.lib.displays.Display
import tech.thatgravyboat.lib.displays.Displays
import tech.thatgravyboat.lib.displays.toColumn
import tech.thatgravyboat.lib.displays.toRow
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.utils.Rect

object PickUpLog : Overlay {

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

    override val name = Text.of("Item Pick Up Log")
    override val position = OverlayPositions.pickupLog
    override val bounds get() = exampleDisplay.getWidth() to exampleDisplay.getHeight()
    override val editBounds: Rect
        get() {
            if (display != null) {
                val (x, y) = position
                val relativeX = if (position.isRight()) exampleDisplay.getWidth() - display!!.getWidth() else 0
                val relativeY = if (position.isBottom()) exampleDisplay.getHeight() - display!!.getHeight() else 0
                return Rect(x + relativeX, y + relativeY, display?.getWidth() ?: 0, display?.getHeight() ?: 0)
            }
            return Rect(position, exampleDisplay.getWidth(), exampleDisplay.getHeight())
        }

    private var display: Display? = null

    private val inventory = mutableMapOf<String, ItemStack>()
    private val addedItems = mutableListOf<PickUpLogItem>()
    private val removedItems = mutableListOf<PickUpLogItem>()

    private var lastWorldSwap = System.currentTimeMillis()

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        if (Overlay.isEditing()) {
            exampleDisplay.render(graphics)
        } else if (display != null) {
            val relativeX = if (position.isRight()) exampleDisplay.getWidth() - display!!.getWidth() else 0
            val relativeY = if (position.isBottom()) exampleDisplay.getHeight() - display!!.getHeight() else 0
            display!!.render(graphics, relativeX, relativeY)
        }
    }

    @Subscription
    @TimePassed("2t")
    @OnlyOnSkyBlock
    fun onTick(event: TickEvent) {
        val flattenedInventory = McPlayer.inventory
            .filterIndexed { index, _ -> index != 8 }
            .filter { !it.isEmpty }
            .groupBy { it.getUniqueId() }
            .filter { it.value.isNotEmpty() }
            .mapValues { (_, value) -> value.first() to value.sumOf(ItemStack::getCount) }
            .map { Triple(it.key, it.value.first, it.value.second) }

        val isSwappingWorlds = lastWorldSwap + 5000 > System.currentTimeMillis()

        val foundIds = mutableSetOf<String>()
        for ((key, item, count) in flattenedInventory) {
            foundIds.add(key)

            if (!isSwappingWorlds) {
                val diff = count - (inventory[key]?.count ?: 0)
                if (diff < 0) {
                    removedItems.add(PickUpLogItem(item.copy(), diff, System.currentTimeMillis()))
                } else if (diff > 0) {
                    addedItems.add(PickUpLogItem(item.copy(), diff, System.currentTimeMillis()))
                }
            }

            inventory[key] = item.copyWithCount(count)
        }

        for (key in (inventory.keys - foundIds)) {
            val item = inventory.remove(key) ?: continue
            if (!isSwappingWorlds) {
                removedItems.add(PickUpLogItem(item.copy(), -item.count, System.currentTimeMillis()))
            }
        }

        addedItems.compactAndCombineTimeAndApply()
        removedItems.compactAndCombineTimeAndApply()

        val currentTime = System.currentTimeMillis()
        val timealive = OverlaysConfig.pickupLog.time * 1000
        addedItems.removeIf { it.time + timealive < currentTime }
        removedItems.removeIf { it.time + timealive < currentTime }
        updateDisplay()
    }

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        lastWorldSwap = System.currentTimeMillis()
        addedItems.clear()
        removedItems.clear()
    }

    private fun updateDisplay() {
        val items = addedItems + removedItems
        if (items.isEmpty()) {
            display = null
            return
        }
        display = items.compact()
            .map { OverlaysConfig.pickupLog.appearance.map { component -> component.display(it) }.toRow(5) }.toColumn()
    }

    private fun MutableList<PickUpLogItem>.compactAndCombineTimeAndApply() {
        val compacted = compactAndCombineTime()
        clear()
        addAll(compacted)
    }

    /**
     * Combines items with the same unique id, combines their time and difference
     */
    private fun MutableList<PickUpLogItem>.compactAndCombineTime() =
        groupBy { it.stack.getUniqueId() }.map { (_, items) ->
            val item = items.reduce { acc, item -> acc.copy(difference = acc.difference + item.difference) }
            item.copy(time = items.maxOf { it.time })
        }

    private fun List<PickUpLogItem>.compact() =
        takeUnless { OverlaysConfig.pickupLog.compact } ?: groupBy { it.stack.getUniqueId() }.map { (_, items) ->
            items.reduce { acc, item -> acc.copy(difference = acc.difference + item.difference) }
        }.filter(PickUpLogItem::isNotEmpty)

    private fun ItemStack.getUniqueId(): String {
        val data: Any? = this.getData(DataTypes.UUID) ?: when (this.getData(DataTypes.ID)) {
            "ENCHANTED_BOOK" -> this.getData(DataTypes.ENCHANTMENTS)
            "POTION" -> "${this.getData(DataTypes.POTION)}/${this.getData(DataTypes.POTION_LEVEL)}"
            null -> this.hoverName.stripped
            else -> "${this.getData(DataTypes.ID)}/${this.getData(DataTypes.RARITY)}/${this.getData(DataTypes.CATEGORY)}"
        }
        return data.toString()
    }
}

