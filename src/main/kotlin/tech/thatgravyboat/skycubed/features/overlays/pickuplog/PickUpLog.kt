package tech.thatgravyboat.skycubed.features.overlays.pickuplog

import me.owdding.ktmodules.Module
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.toColumn
import me.owdding.lib.displays.toRow
import me.owdding.lib.overlays.Rect
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.hypixel.SacksChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.getArmor
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.PickupLogOverlayConfig
import tech.thatgravyboat.skycubed.utils.RegisterOverlay
import tech.thatgravyboat.skycubed.utils.BackgroundLessSkyCubedOverlay

@Module
@RegisterOverlay
object PickUpLog : BackgroundLessSkyCubedOverlay {

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
    override val actualBounds get() = exampleDisplay.getWidth() to exampleDisplay.getHeight()
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
        if (this.isEditing()) {
            exampleDisplay.render(graphics)
        } else if (display != null && PickupLogOverlayConfig.enabled) {
            val relativeX = if (position.isRight()) exampleDisplay.getWidth() - display!!.getWidth() else 0
            val relativeY = if (position.isBottom()) exampleDisplay.getHeight() - display!!.getHeight() else 0
            display!!.render(graphics, relativeX, relativeY)
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onTick(event: TickEvent) {
        if (!PickupLogOverlayConfig.enabled) {
            display = null
            return
        }
        if (McScreen.self != null && McScreen.self !is ChatScreen) return

        val flattenedInventory = listOfNotNull(McPlayer.inventory, McPlayer.self?.getArmor(), listOfNotNull(McPlayer.self?.offhandItem))
            .flatten()
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
        syncTime()

        val currentTime = System.currentTimeMillis()
        val timealive = PickupLogOverlayConfig.time * 1000
        addedItems.removeIf { it.time + timealive < currentTime }
        removedItems.removeIf { it.time + timealive < currentTime }
        updateDisplay()
    }

    @Subscription
    fun onSack(event: SacksChangeEvent) {
        if (!PickupLogOverlayConfig.sackItems) return

        event.changedItems.forEach { (item, diff) ->
            val stack = RepoItemsAPI.getItem(item)
            if (diff < 0) {
                removedItems.add(PickUpLogItem(stack, diff, System.currentTimeMillis()))
            } else if (diff > 0) {
                addedItems.add(PickUpLogItem(stack, diff, System.currentTimeMillis()))
            }
        }
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
        display = items.compact().map { PickupLogOverlayConfig.appearance.map { component -> component.display(it) }.toRow(5) }.toColumn()
    }

    /**
     * Syncs the time of the same item ids in added and removed lists.
     */
    private fun syncTime() {
        (addedItems + removedItems).groupBy { it.stack.getUniqueId() }.forEach { (_, items) ->
            if (items.size < 2) return@forEach
            val maxTime = items.maxOf { it.time }
            items.forEach { it.time = maxTime }
        }
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
            item.time = items.maxOf { it.time }
            item
        }

    private fun List<PickUpLogItem>.compact() =
        takeUnless { PickupLogOverlayConfig.compact } ?: groupBy { it.stack.getUniqueId() }.map { (_, items) ->
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

