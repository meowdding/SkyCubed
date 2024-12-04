package tech.thatgravyboat.skycubed.features.overlays

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerHotbarChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerInventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.displays.Display
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.PickUpLogConfig

object PickUpLog : Overlay {
    override val name = Text.of("Item Pick Up Log")
    override val position = PickUpLogConfig.position
    override val bounds get() = dimension

    private var dimension = 100 to 100

    private var display: Display? = null

    @Subscription
    fun onInvChange(event: PlayerInventoryChangeEvent) {
        println("bals")
        display = Displays.item(event.item)
    }

    // todo remove once sbapi is updated
    @Subscription
    fun onHotbarChange(event: PlayerHotbarChangeEvent) {
        println("bals2")
        display = Displays.item(event.item)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        display?.render(graphics, position.x, position.y)
    }

}