package tech.thatgravyboat.skycubed.features.overlays

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Alignment
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.withPadding
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.profile.sacks.SacksAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.api.repo.SackCodecs
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.config.overlays.SackOverlayConfig
import tech.thatgravyboat.skycubed.features.screens.SackHudEditScreen
import tech.thatgravyboat.skycubed.utils.CachedValue
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures
import kotlin.time.Duration.Companion.seconds

object SackOverlay : Overlay {

    override val name: Component = Text.of("Sack Overlay")
    override val position: Position get() = OverlayPositions.sack
    override val bounds get() = display.get().getWidth() to display.get().getHeight()
    override val enabled: Boolean get() = SackOverlayConfig.enabled && SackOverlayConfig.sackItems.isNotEmpty()

    private val display = CachedValue(1.seconds) {
        val display = DisplayFactory.vertical {
            SackOverlayConfig.sackItems.forEach { item ->
                val stack = SackCodecs.sackItems[item] ?: return@forEach
                horizontal(5, Alignment.CENTER) {
                    display(Displays.item(stack))
                    string(stack.hoverName)
                    val sackItems = SacksAPI.sackItems[item] ?: 0
                    string(Text.of("x${sackItems.toFormattedString()}") { color = TextColor.PINK })
                }
            }
        }

        if (SackOverlayConfig.background) {
            Displays.background(SkyCubedTextures.backgroundBox, display.withPadding(4))
        } else {
            display.withPadding(4)
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.fill(0, 0, bounds.first, bounds.second, 0x50000000)
        display.get().render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        it.button(Text.of("Open Sack Edit Screen")) {
            McClient.setScreen(SackHudEditScreen())
        }
        it.button(Text.of("${if (SackOverlayConfig.background) "Disable" else "Enable"} Custom Background")) {
            SackOverlayConfig.background = !SackOverlayConfig.background
            display.invalidate()
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.reset()
        }
    }
}
