package tech.thatgravyboat.skycubed.features.commands.hypixel

import com.google.gson.JsonArray
import kotlinx.coroutines.runBlocking
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData


object HypixelCommands {

    private val commands: MutableList<LiteralHypixelCommand> = mutableListOf()

    init {
        runBlocking {
            runCatching {
                val file = this.javaClass.getResourceAsStream("/repo/commands.json")?.readJson<JsonArray>() ?: return@runCatching
                file.toData(LiteralHypixelCommand.CODEC.listOf())?.let(commands::addAll)
            }
        }
    }

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        commands.forEach { command ->
            val serverCommands = McClient.serverCommands ?: return@forEach
            if (command.values.none { serverCommands.root.children.any { node -> node.name == it } }) return@forEach

            command.values.forEach {
                McClient.serverCommands?.root?.children?.removeIf { node -> node.name == it }
            }

            command.toCommand().forEach {
                event.register(it)
            }
        }
    }
}