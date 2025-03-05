package tech.thatgravyboat.skycubed.features.overlays

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.utils.pushPop

object WindOverlay {

    private var windCompassComponent: Component? = null

    @Subscription
    @OnlyIn(SkyBlockIsland.DWARVEN_MINES, SkyBlockIsland.CRYSTAL_HOLLOWS)
    fun onHudRender(event: RenderHudEvent) {
        if (!OverlaysConfig.windOverlay) return
        windCompassComponent?.let {
            event.graphics.pushPop {
                translate(
                    (event.graphics.guiWidth() / 2).toFloat(),
                    (event.graphics.guiHeight() / 2).toFloat(),
                    0.0F
                )
                scale(1.2F, 1.2F, 1.2F)

                val width = McFont.width(it)
                event.graphics.drawStringWithBackdrop(McFont.self, it, -width / 2, -10, width, 0xffffff)
            }
        }
    }

    @Subscription
    @TimePassed("2t")
    @OnlyIn(SkyBlockIsland.DWARVEN_MINES, SkyBlockIsland.CRYSTAL_HOLLOWS)
    fun onTick(event: TickEvent) {
        val scoreboard = McClient.scoreboard.toList()
        val index = scoreboard.indexOfFirst { it.stripped == "Wind Compass" }.takeUnless { it == -1 } ?: return

        windCompassComponent = scoreboard.getOrNull(index + 1)
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.DWARVEN_MINES, SkyBlockIsland.CRYSTAL_HOLLOWS)
    fun onChatMessage(event: ChatReceivedEvent) {
        if (event.text.contains("GONE WITH THE WIND ENDED!")) {
            windCompassComponent = null
        }
    }

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        windCompassComponent = null
    }
}
