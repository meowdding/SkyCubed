package tech.thatgravyboat.skycubed

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import kotlinx.coroutines.runBlocking
import me.owdding.lib.utils.DataPatcher
import me.owdding.skycubed.generated.SkyCubedCodecs
import me.owdding.skycubed.generated.SkyCubedModules
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.SharedConstants
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.utils.json.Json
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.net.URI
import java.nio.file.Files
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

object SkyCubed : ModInitializer, Logger by LoggerFactory.getLogger("SkyCubed") {

    val mod: ModContainer = FabricLoader.getInstance().getModContainer("skycubed").orElseThrow()
    val repoPatcher: DataPatcher?

    init {
        var patch: DataPatcher?
        try {
            patch = DataPatcher(URI.create("https://patches.owdding.me/${SharedConstants.getCurrentVersion().name.replace(".", "_")}.json").toURL(), mod)
        } catch (e: Exception) {
            error("Failed to load patches!", e)
            patch = null
        }
        repoPatcher = patch
    }

    override fun onInitialize() {
        SkyCubedModules.init { SkyBlockAPI.eventBus.register(it) }
    }

    fun id(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath("skycubed", path)
    }

    inline fun <reified T : Any> loadFromRepo(file: String): T? = runBlocking {
        try {
            val json = mod.findPath("repo/$file.json").orElseThrow()?.let(Files::readString)?.readJson<JsonElement>() ?: return@runBlocking null
            try {
                repoPatcher?.patch(json, file)
            } catch (e: Exception) {
                error("Failed to apply patches for file $file", e)
                return@runBlocking null
            }
            return@runBlocking Json.gson.fromJson(json, typeOf<T>().javaType)
        } catch (e: Exception) {
            error("Failed to load $file from repo", e)
            return@runBlocking null
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
