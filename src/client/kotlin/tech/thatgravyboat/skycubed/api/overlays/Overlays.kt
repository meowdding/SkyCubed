package tech.thatgravyboat.skycubed.api.overlays

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.features.info.InfoOverlay
import tech.thatgravyboat.skycubed.features.overlays.*
import tech.thatgravyboat.skycubed.features.overlays.commissions.CommissionsOverlay
import tech.thatgravyboat.skycubed.features.overlays.pickuplog.PickUpLog
import tech.thatgravyboat.skycubed.utils.pushPop

object Overlays {

    private val overlays = mutableListOf<Overlay>()

    fun register(overlay: Overlay) {
        overlays.add(overlay)
    }

    private fun isOverlayScreen(screen: Screen?, mouseX: Int, mouseY: Int): Boolean {
        return (screen is ChatScreen && !isWithinChatBounds(mouseX, mouseY)) || screen is EditOverlaysScreen
    }

    private fun isWithinChatBounds(mouseX: Int, mouseY: Int): Boolean {
        val window = McClient.window
        val chat = McClient.chat

        val height = chat.height
        val width = chat.width
        val x = 0
        val y = window.guiScaledHeight - 40 - height

        return mouseX in x..width && mouseY in y..window.guiScaledHeight - 40
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onHudRender(event: RenderHudEvent) {
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

            if (isOverlayScreen(screen, mouseX.toInt(), mouseY.toInt()) && rect.contains(mouseX.toInt(), mouseY.toInt())) {
                graphics.fill(rect.x, rect.y, rect.right, rect.bottom, 0x50000000)
                graphics.renderOutline(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2, 0xFFFFFFFF.toInt())
                if (it.moveable) {
                    screen!!.setTooltipForNextRenderPass(Text.multiline(
                        it.name,
                        CommonText.EMPTY,
                        Text.translatable("ui.skycubed.overlay.edit"),
                        Text.of("SkyCubed") {
                            this.color = TextColor.BLUE
                            this.withStyle(ChatFormatting.ITALIC)
                        }
                    ))
                } else {
                    screen!!.setTooltipForNextRenderPass(it.name)
                }
            }
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onMouseClick(event: ScreenMouseClickEvent.Pre) {
        if (!isOverlayScreen(event.screen, event.x.toInt(), event.y.toInt())) return

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
                callback { McClient.setScreen(EditOverlaysScreen()) }
            }
        }
    }

    init {
        register(PlayerRpgOverlay)
        register(CommissionsOverlay)
        register(InfoOverlay)
        register(DialogueOverlay)
        register(PickUpLog)
        register(MinimapOverlay)
        register(MovableHotbar)
        TextOverlay.overlays.forEach(::register)
    }
}