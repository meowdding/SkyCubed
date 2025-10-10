package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import me.owdding.ktmodules.Module
import me.owdding.lib.overlays.EditOverlaysScreen
import net.fabricmc.loader.api.FabricLoader
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.SkyCubed.VERSION
import tech.thatgravyboat.skycubed.SkyCubed.sendWithPrefix
import java.nio.file.Files

@Module
object ConfigManager {

    private val configurator = Configurator("skycubed")
    private val config = run {
        Files.createDirectories(FabricLoader.getInstance().configDir.resolve("skycubed"))
    }.let {
        Config.register(configurator)
    }

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.register("skycubed") {
            thenCallback("version") {
                Text.of("Version: $VERSION").withColor(TextColor.GRAY).sendWithPrefix()
            }
            thenCallback("overlays") {
                McClient.setScreen(EditOverlaysScreen(SkyCubed.MOD_ID))
            }

            callback {
                McClient.setScreen(ResourcefulConfigScreen.make(config).build())
            }
        }
    }

    fun save() {
        config.save()
    }
}
