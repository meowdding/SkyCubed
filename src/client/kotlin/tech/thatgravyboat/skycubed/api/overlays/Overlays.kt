package tech.thatgravyboat.skycubed.api.overlays

import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.features.rpg.PlayerRpgOverlay
import tech.thatgravyboat.skycubed.utils.pushPop

object Overlays {

    private val overlays = mutableListOf<Overlay>()

    fun register(overlay: Overlay) {
        overlays.add(overlay)
    }

    private fun isOverlayScreen(screen: Screen?): Boolean {
        return screen is ChatScreen
    }

    @Subscription
    fun onHudRender(event: RenderHudEvent) {
        if (!LocationAPI.isOnSkyblock) return

        val graphics = event.graphics
        val screen = McScreen.self
        val (mouseX, mouseY) = McClient.mouse
        overlays.forEach {
            if (!it.enabled) return@forEach
            val (x, y) = it.position
            val (width, height) = it.bounds
            graphics.pushPop {
                translate(x.toFloat(), y.toFloat(), 0f)
                it.render(graphics, mouseX.toInt(), mouseY.toInt())
            }

            if (isOverlayScreen(screen) && mouseX.toInt() - x in 0..width && mouseY.toInt() - y in 0..height) {
                graphics.fill(x, y, x + width, y + height, 0x50000000)
                graphics.renderOutline(x - 1, y - 1, width + 2, height + 2, 0xFFFFFFFF.toInt())
                screen!!.setTooltipForNextRenderPass(Text.multiline(
                    it.name,
                    CommonText.EMPTY,
                    Component.translatable("ui.skycubed.overlay.edit")
                ))
            }
        }
    }

    @Subscription
    fun onMouseClick(event: ScreenMouseClickEvent.Pre) {
        if (!LocationAPI.isOnSkyblock) return
        if (!isOverlayScreen(event.screen)) return

        for (overlay in overlays) {
            if (!overlay.enabled) continue
            val (x, y) = overlay.position
            val (width, height) = overlay.bounds

            if ((event.x - x).toInt() in 0..width && (event.y - y).toInt() in 0..height) {
                McClient.self.setScreen(OverlayScreen(overlay))
                return
            }
        }
    }

    init {
        register(PlayerRpgOverlay)
    }
}