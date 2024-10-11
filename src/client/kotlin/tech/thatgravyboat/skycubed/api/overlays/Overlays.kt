package tech.thatgravyboat.skycubed.api.overlays

import com.teamresourceful.resourcefullibkt.client.pushPop
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.features.info.InfoOverlay
import tech.thatgravyboat.skycubed.features.overlays.DialogueOverlay
import tech.thatgravyboat.skycubed.features.overlays.PlayerRpgOverlay
import tech.thatgravyboat.skycubed.features.overlays.TextOverlay
import tech.thatgravyboat.skycubed.features.overlays.commissions.CommissionsOverlay

object Overlays {

    private val overlays = mutableListOf<Overlay>()

    fun register(overlay: Overlay) {
        overlays.add(overlay)
    }

    private fun isOverlayScreen(screen: Screen?): Boolean {
        return screen is ChatScreen || screen is EditOverlaysScreen
    }

    @Subscription
    fun onHudRender(event: RenderHudEvent) {
        if (!LocationAPI.isOnSkyblock) return
        if (McClient.self.options.hideGui) return

        val graphics = event.graphics
        val screen = McScreen.self
        val (mouseX, mouseY) = McClient.mouse
        overlays.forEach {
            if (!it.enabled) return@forEach
            val (x, y) = it.position
            graphics.pushPop {
                translate(x.toFloat(), y.toFloat(), 0f)
                scale(it.position.scale, it.position.scale, 1f)
                it.render(graphics, mouseX.toInt(), mouseY.toInt())
            }

            val rect = it.editBounds * it.position.scale

            if (isOverlayScreen(screen) && rect.contains(mouseX.toInt(), mouseY.toInt())) {
                graphics.fill(rect.x, rect.y, rect.right, rect.bottom, 0x50000000)
                graphics.renderOutline(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2, 0xFFFFFFFF.toInt())
                if (it.moveable) {
                    screen!!.setTooltipForNextRenderPass(Text.multiline(
                        it.name,
                        CommonText.EMPTY,
                        Component.translatable("ui.skycubed.overlay.edit")
                    ))
                } else {
                    screen!!.setTooltipForNextRenderPass(it.name)
                }
            }
        }
    }

    @Subscription
    fun onMouseClick(event: ScreenMouseClickEvent.Pre) {
        if (!LocationAPI.isOnSkyblock) return
        if (!isOverlayScreen(event.screen)) return

        for (overlay in overlays.reversed()) {
            if (!overlay.enabled) continue
            if (!overlay.moveable) continue
            val rect = overlay.editBounds * overlay.position.scale

            if (rect.contains(event.x, event.y)) {
                McClient.setScreen(OverlayScreen(overlay))
                return
            }
        }
    }

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        event.register("skycubed") {
            then("overlays") {
                callback {
                    McClient.setScreen(EditOverlaysScreen())
                }
            }
        }
    }

    init {
        register(PlayerRpgOverlay)
        register(CommissionsOverlay)
        register(InfoOverlay)
        register(DialogueOverlay)
        TextOverlay.overlays.forEach(::register)
    }
}