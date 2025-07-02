package tech.thatgravyboat.skycubed.compatibility

import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.screen.OverlayDecider
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.InteractionResult
import tech.thatgravyboat.skycubed.features.equipment.wardobe.WardrobeFeature
import tech.thatgravyboat.skycubed.features.equipment.wardobe.WardrobeScreen
import kotlin.reflect.full.isSuperclassOf

@Deprecated("should use mlib")
private object REIScreenHider : OverlayDecider {

    override fun <R : Screen> isHandingScreen(screen: Class<R>): Boolean = ContainerScreen::class.isSuperclassOf(screen.kotlin)

    override fun <R : Screen> shouldScreenBeOverlaid(screen: R): InteractionResult {
        if (screen == WardrobeScreen.screen && !WardrobeFeature.isEditing) {
            return InteractionResult.FAIL
        }
        return InteractionResult.PASS
    }
}

object REICompatability : REIClientPlugin {

    override fun registerScreens(registry: ScreenRegistry) {
        registry.registerDecider(REIScreenHider)
    }
}
