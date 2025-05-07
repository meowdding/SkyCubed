package tech.thatgravyboat.skycubed

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import kotlinx.coroutines.runBlocking
import me.owdding.skycubed.generated.SkyCubedCodecs
import me.owdding.skycubed.generated.SkyCubedModules
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skycubed.api.overlays.Overlays
import tech.thatgravyboat.skycubed.config.ConfigManager
import tech.thatgravyboat.skycubed.features.chat.ChatManager
import tech.thatgravyboat.skycubed.features.commands.hypixel.HypixelCommands
import tech.thatgravyboat.skycubed.features.equipment.EquipmentManager
import tech.thatgravyboat.skycubed.features.equipment.wardobe.WardrobeFeature
import tech.thatgravyboat.skycubed.features.info.foraging.ParkInfoOverlay
import tech.thatgravyboat.skycubed.features.items.CooldownManager
import tech.thatgravyboat.skycubed.features.items.ItemBarManager
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.misc.ElementHider
import tech.thatgravyboat.skycubed.features.misc.UpdateChecker
import tech.thatgravyboat.skycubed.features.notifications.NotificationManager
import tech.thatgravyboat.skycubed.features.overlays.DialogueOverlay
import tech.thatgravyboat.skycubed.features.overlays.map.DungeonMap
import tech.thatgravyboat.skycubed.features.overlays.map.MinimapOverlay
import tech.thatgravyboat.skycubed.features.overlays.mining.PityOverlay
import tech.thatgravyboat.skycubed.features.overlays.mining.WindOverlay
import tech.thatgravyboat.skycubed.features.overlays.pickuplog.PickUpLog
import tech.thatgravyboat.skycubed.features.tablist.CompactTablist
import tech.thatgravyboat.skycubed.utils.ContributorHandler
import java.nio.file.Files

object SkyCubed : ModInitializer, Logger by LoggerFactory.getLogger("SkyCubed") {

    val mod = FabricLoader.getInstance().getModContainer("skycubed").orElseThrow()

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
        SkyBlockAPI.eventBus.register(CompactTablist)
        SkyBlockAPI.eventBus.register(PickUpLog)
        SkyBlockAPI.eventBus.register(MinimapOverlay)
        SkyBlockAPI.eventBus.register(WindOverlay)
        SkyBlockAPI.eventBus.register(DungeonMap)
        SkyBlockAPI.eventBus.register(ContributorHandler)
        SkyBlockAPI.eventBus.register(UpdateChecker)
        SkyBlockAPI.eventBus.register(WardrobeFeature)
        SkyBlockAPI.eventBus.register(ParkInfoOverlay)
        SkyBlockAPI.eventBus.register(PityOverlay)

        SkyCubedModules.init { SkyBlockAPI.eventBus.register(it) }
    }

    fun id(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("skycubed", path)
    }

    inline fun <reified T : Any> loadFromRepo(file: String) = runBlocking {
        try {
            mod.findPath("repo/$file.json").orElseThrow()?.let(Files::readString)?.readJson<T>() ?: return@runBlocking null
        } catch (e: Exception) {
            error("Failed to load $file from repo", e)
            null
        }
    }

    internal inline fun <reified T : Any> loadRepoData(file: String): T {
        return loadRepoData<T, T>(file) { it }
    }

    internal inline fun <reified T : Any, B : Any> loadRepoData(file: String, modifier: (Codec<T>) -> Codec<B>): B {
        return loadFromRepo<JsonElement>(file).toDataOrThrow(SkyCubedCodecs.getCodec<T>().let(modifier))
    }

    internal inline fun <B : Any> loadRepoData(file: String, supplier: () -> Codec<B>): B {
        return loadFromRepo<JsonElement>(file).toDataOrThrow(supplier())
    }

    internal fun <B : Any> loadRepoData(file: String, codec: Codec<B>): B {
        return loadFromRepo<JsonElement>(file).toDataOrThrow(codec)
    }
}
