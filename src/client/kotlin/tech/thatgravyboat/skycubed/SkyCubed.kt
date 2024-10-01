package tech.thatgravyboat.skycubed

import com.mojang.logging.LogUtils
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skycubed.api.overlays.Overlays
import tech.thatgravyboat.skycubed.config.ConfigManager
import tech.thatgravyboat.skycubed.features.CooldownManager
import tech.thatgravyboat.skycubed.features.ElementHider
import tech.thatgravyboat.skycubed.features.notifications.NotificationManager

class SkyCubed : ModInitializer {

    override fun onInitialize() {
        SkyBlockAPI.eventBus.register(ConfigManager)
        SkyBlockAPI.eventBus.register(Overlays)
        SkyBlockAPI.eventBus.register(ElementHider)
        SkyBlockAPI.eventBus.register(CooldownManager)
        SkyBlockAPI.eventBus.register(NotificationManager)
    }

    companion object {

        val logger = LogUtils.getLogger()

        fun id(path: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath("skycubed", path)
        }
    }
}
