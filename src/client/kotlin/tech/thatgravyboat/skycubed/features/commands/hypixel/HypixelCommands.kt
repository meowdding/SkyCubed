package tech.thatgravyboat.skycubed.features.commands.hypixel

import com.google.gson.JsonArray
import com.mojang.brigadier.tree.RootCommandNode
import kotlinx.coroutines.runBlocking
import net.minecraft.commands.SharedSuggestionProvider
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skycubed.SkyCubed

object HypixelCommands {

    private val commands: MutableList<LiteralHypixelCommand> = mutableListOf()
    private val roots: MutableList<String> = mutableListOf()

    init {
        runBlocking {
            try {
                val file = SkyCubed.loadFromRepo<JsonArray>("commands")
                file.toDataOrThrow(LiteralHypixelCommand.CODEC.listOf())?.let(commands::addAll)
            } catch (e: Exception) {
                println(e)
            }

            for (command in commands) {
                roots.addAll(command.values)
            }
        }
    }

    fun removeServerCommands(root: RootCommandNode<SharedSuggestionProvider>) {
        commands.forEach { command ->
            if (command.values.none { root.getChild(it) != null }) return@forEach

            command.values.forEach { value ->
                root.children.removeIf { node -> node.name.equals(value, true) }
            }
        }
    }

    fun isRootCommand(command: String): Boolean {
        return roots.contains(command)
    }

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        commands.forEach { command ->
            command.toCommand().forEach {
                event.register(it)
            }
        }
    }
}