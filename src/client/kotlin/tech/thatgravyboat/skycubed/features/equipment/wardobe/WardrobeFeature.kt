package tech.thatgravyboat.skycubed.features.equipment.wardobe

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenBackgroundEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseReleasedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.config.screens.ScreensConfig

object WardrobeFeature {

    private val regex = Regex("Wardrobe \\((?<currentPage>\\d+)/\\d+\\)")
    var isEditing = false

    @Subscription
    fun onContainerRender(event: RenderScreenBackgroundEvent) {
        if (!event.screen.isEnabled() || isEditing) return

        var currentPage = -1
        regex.match(event.screen.title.stripped, "currentPage") { (page) ->
            currentPage = page.toIntOrNull() ?: 0
        }

        if (currentPage == -1) return

        event.cancel()

        WardrobeScreen.screen = event.screen
        WardrobeScreen.currentPage = currentPage

        val (mouseX, mouseY) = McClient.mouse
        WardrobeScreen.init(McClient.self, event.screen.width, event.screen.height)
        WardrobeScreen.renderWithTooltip(event.graphics, mouseX.toInt(), mouseY.toInt(), 0f)
    }

    @Subscription
    fun onContainerClick(event: ScreenMouseClickEvent.Pre) {
        if (!event.screen.isEnabled() || isEditing) return
        event.cancel()

        WardrobeScreen.mouseClicked(event.x, event.y, event.button)
    }

    @Subscription
    fun onContainerClick(event: ScreenMouseReleasedEvent.Pre) {
        if (!event.screen.isEnabled() || isEditing) return
        event.cancel()

        WardrobeScreen.mouseReleased(event.x, event.y, event.button)
    }

    @Subscription
    fun onContainerKey(event: ScreenKeyPressedEvent.Pre) {
        if (!event.screen.isEnabled()) return

        event.cancel()

        if (isEditing) {
            when (event.key) {
                InputConstants.KEY_ESCAPE -> isEditing = false
            }
        } else {
            when (event.key) {
                InputConstants.KEY_ESCAPE, InputConstants.KEY_E -> {
                    event.screen.onClose()
                    WardrobeScreen.screen = null
                }
            }
        }
    }

    @Subscription
    fun onScreenInit(event: ScreenInitializedEvent) {
        if (event.screen.isEnabled()) return
        isEditing = false
    }

    private fun Screen.isEnabled() =
        this.title.stripped.lowercase().startsWith("wardrobe") && ScreensConfig.wardrobe.enabled
}