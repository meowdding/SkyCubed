package tech.thatgravyboat.skycubed

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import kotlinx.coroutines.runBlocking
import me.owdding.ktmodules.Module
import me.owdding.lib.events.overlay.FinishOverlayEditingEvent
import me.owdding.lib.overlays.Overlays
import me.owdding.lib.utils.MeowddingUpdateChecker
import me.owdding.skycubed.generated.SkyCubedCodecs
import me.owdding.skycubed.generated.SkyCubedModules
import me.owdding.skycubed.generated.SkyCubedRegisteredOverlays
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.url
import tech.thatgravyboat.skycubed.config.ConfigManager
import tech.thatgravyboat.skycubed.features.overlays.TextOverlay
import java.nio.file.Files
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

@Module
object SkyCubed : ModInitializer, Logger by LoggerFactory.getLogger("SkyCubed") {

    val mod: ModContainer = FabricLoader.getInstance().getModContainer("skycubed").orElseThrow()
    val MOD_ID: String get() = mod.metadata.id
    val VERSION: String = mod.metadata.version.friendlyString

    val prefix = Text.of {
        append("[") { color = TextColor.GRAY }
        append("SkyCubed") { color = TextColor.YELLOW }
        append("] ") { color = TextColor.GRAY }
    }

    fun Component.sendWithPrefix() = Text.join(prefix, this).send()

    override fun onInitialize() {
        SkyCubedModules.init { SkyBlockAPI.eventBus.register(it) }
        MeowddingUpdateChecker("znwUKvZc", mod, ::updateMessage)

        SkyCubedRegisteredOverlays.collected.forEach { Overlays.register(it) }
        TextOverlay.overlays.forEach { Overlays.register(it) }
    }

    fun updateMessage(link: String, current: String, new: String) {
        fun MutableComponent.withLink() = this.apply {
            this.url = link
            this.hover = Text.of(link).withColor(TextColor.GRAY)
        }

        McClient.runNextTick {
            Text.of().send()
            Text.join(
                "New version found! (",
                Text.of(current).withColor(TextColor.RED),
                Text.of(" -> ").withColor(TextColor.GRAY),
                Text.of(new).withColor(TextColor.GREEN),
                ")",
            ).withLink().sendWithPrefix()
            Text.of("Click to download.").withLink().sendWithPrefix()
            Text.of().send()
        }
    }

    @Subscription
    private fun onOverlayEditFinish(event: FinishOverlayEditingEvent) {
        if (event.modId == MOD_ID)
            ConfigManager.save()

    }

    fun id(path: String): Identifier {
        return Identifier.fromNamespaceAndPath("skycubed", path)
    }

    fun olympus(path: String): Identifier = Identifier.fromNamespaceAndPath("olympus", path)
}
