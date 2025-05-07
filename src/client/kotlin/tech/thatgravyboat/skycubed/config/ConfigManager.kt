package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import net.fabricmc.loader.api.FabricLoader
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.nio.file.Files

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
            callback {
                McClient.setScreen(ResourcefulConfigScreen.get(null, config))
            }
        }
    }

    fun save() {
        config.save()
    }
}