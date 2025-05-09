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
import java.nio.file.Files

object SkyCubed : ModInitializer, Logger by LoggerFactory.getLogger("SkyCubed") {

    val mod = FabricLoader.getInstance().getModContainer("skycubed").orElseThrow()

    override fun onInitialize() {
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
