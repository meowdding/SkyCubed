package tech.thatgravyboat.skycubed

import com.mojang.logging.LogUtils
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skycubed.api.overlays.Overlays
import tech.thatgravyboat.skycubed.config.ConfigManager
import tech.thatgravyboat.skycubed.features.CooldownManager
import tech.thatgravyboat.skycubed.features.ElementHider
import tech.thatgravyboat.skycubed.features.ItemBarManager
import tech.thatgravyboat.skycubed.features.RepoDebug
import tech.thatgravyboat.skycubed.features.chat.ChatManager
import tech.thatgravyboat.skycubed.features.commands.hypixel.HypixelCommands
import tech.thatgravyboat.skycubed.features.equipment.EquipmentManager
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.notifications.NotificationManager
import tech.thatgravyboat.skycubed.features.overlays.DialogueOverlay

object SkyCubed : ModInitializer {

    override fun onInitialize() {
        SkyBlockAPI.eventBus.register(ConfigManager)
        SkyBlockAPI.eventBus.register(Overlays)
        SkyBlockAPI.eventBus.register(ElementHider)
        SkyBlockAPI.eventBus.register(CooldownManager)
        SkyBlockAPI.eventBus.register(NotificationManager)
        SkyBlockAPI.eventBus.register(ItemBarManager)
        SkyBlockAPI.eventBus.register(HypixelCommands)
        SkyBlockAPI.eventBus.register(EquipmentManager)
        SkyBlockAPI.eventBus.register(ChatManager)
        SkyBlockAPI.eventBus.register(DialogueOverlay)
        SkyBlockAPI.eventBus.register(Maps)
        SkyBlockAPI.eventBus.register(RepoDebug)
    }


    val logger = LogUtils.getLogger()

    fun id(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("skycubed", path)
    }
}
