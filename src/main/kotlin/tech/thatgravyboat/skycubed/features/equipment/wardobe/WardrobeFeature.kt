package tech.thatgravyboat.skycubed.features.equipment.wardobe

import com.mojang.blaze3d.platform.InputConstants
import me.owdding.ktmodules.Module
import me.owdding.lib.compat.REIRenderOverlayEvent
import me.owdding.lib.platform.screens.KeyEvent
import me.owdding.lib.platform.screens.MouseButtonEvent
import me.owdding.lib.platform.screens.mouseClicked
import me.owdding.lib.platform.screens.mouseReleased
import me.owdding.lib.utils.MeowddingKeybind
import me.owdding.lib.utils.matches
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
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
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.config.screens.WardrobeConfig
import tech.thatgravyboat.skycubed.utils.Utils
import tech.thatgravyboat.skycubed.utils.Utils.fullyRender

@Module
object WardrobeFeature {

    private val regex = Regex("Wardrobe \\((?<currentPage>\\d+)/\\d+\\)")
    var isEditing = false

    val wardrobeKeyBinds: MutableMap<MeowddingKeybind, Int> = mutableMapOf()

    init {
        for (slot in 1..9) {
            wardrobeKeyBinds[
                MeowddingKeybind(
                    SkyCubed.id("wardrobe"),
                    "skycubed.key.wardrobe.slot.$slot",
                    InputConstants.UNKNOWN.value
                )
            ] = slot + 35
        }
        wardrobeKeyBinds[
            MeowddingKeybind(
                SkyCubed.id("wardrobe"),
                "skycubed.key.wardrobe.prev_page",
                InputConstants.UNKNOWN.value
            )
        ] = 45
        wardrobeKeyBinds[
            MeowddingKeybind(
                SkyCubed.id("wardrobe"),
                "skycubed.key.wardrobe.next_page",
                InputConstants.UNKNOWN.value
            )
        ] = 53
    }

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
        //? if > 1.21.10 {
        WardrobeScreen.init(event.screen.width, event.screen.height)
        //?} else
        /*WardrobeScreen.init(McClient.self, event.screen.width, event.screen.height)*/
        WardrobeScreen.fullyRender(event.graphics, mouseX.toInt(), mouseY.toInt(), 0f)
    }

    @Subscription
    fun onContainerClick(event: ScreenMouseClickEvent.Pre) {
        if (!event.screen.isEnabled() || isEditing) return
        event.cancel()

        WardrobeScreen.mouseClicked(MouseButtonEvent(event.x, event.y, event.button), false)
    }

    @Subscription
    fun onContainerClick(event: ScreenMouseReleasedEvent.Pre) {
        if (!event.screen.isEnabled() || isEditing) return
        event.cancel()

        WardrobeScreen.mouseReleased(MouseButtonEvent(event.x, event.y, event.button))
    }

    @Subscription
    fun onContainerKey(event: ScreenKeyPressedEvent.Pre) {
        if (!event.screen.isEnabled()) return

        event.cancel()

        val shouldClose = event.key == InputConstants.KEY_ESCAPE || McClient.options.keyInventory.matches(KeyEvent(event.key, event.scanCode, 0))

        if (isEditing) {
            if (shouldClose) {
                isEditing = false
            }
        } else {
            if (shouldClose) {
                event.screen.onClose()
                WardrobeScreen.screen = null
            }
        }

        val clickedWardrobeKey = wardrobeKeyBinds.keys.firstOrNull {
            it.matches(event)
        }

        if (clickedWardrobeKey != null) {
            val slotIndex = wardrobeKeyBinds[clickedWardrobeKey] ?: return
            WardrobeScreen.clickWardrobeSlot(slotIndex)
        }
    }

    @Subscription
    fun onScreenInit(event: ScreenInitializedEvent) {
        if (event.screen.isEnabled()) {
            ScreenEvents.remove(event.screen).register {
                Utils.resetCursor()
            }
        } else {
            isEditing = false
        }
    }

    @Subscription
    fun onReiOverlay(event: REIRenderOverlayEvent) {
        if (event.screen == WardrobeScreen.screen && !isEditing) {
            event.cancel()
        }
    }

    private fun Screen.isEnabled() = this.title.stripped.lowercase().startsWith("wardrobe") && WardrobeConfig.enabled
}
