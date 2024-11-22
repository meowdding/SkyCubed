package tech.thatgravyboat.skycubed.features.commands.hypixel

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.function.Function

sealed interface HypixelCommand {

    fun toCommand(): List<ArgumentBuilder<FabricClientCommandSource, *>>

    companion object {

        val CODEC: Codec<HypixelCommand> = Codec.lazyInitialized {
            Codec.either(ArgumentHypixelCommand.CODEC, LiteralHypixelCommand.CODEC)
                .flatComapMap(
                    { it.map(Function.identity(), Function.identity()) },
                    { when (it) {
                        is ArgumentHypixelCommand -> DataResult.success(Either.left(it))
                        is LiteralHypixelCommand -> DataResult.success(Either.right(it))
                        else -> DataResult.error { "Unknown command type: $it" }
                    } }
                )
        }
    }

}

data class ArgumentHypixelCommand(
    val name: String,
    val greedy: Boolean = false,
    val suggestions: HypixelCommandSuggestion,
    val children: List<HypixelCommand>,
) : HypixelCommand {

    override fun toCommand(): List<ArgumentBuilder<FabricClientCommandSource, *>> {
        val argument = if (greedy) StringArgumentType.greedyString() else StringArgumentType.string()
        return listOf(ClientCommandManager.argument(name, argument).apply {
            suggests { _, builder ->
                SharedSuggestionProvider.suggest(suggestions(), builder)
            }

            executes {
                McClient.self.connection?.send(ServerboundChatCommandPacket(it.input))
                1
            }

            children.forEach { child -> child.toCommand().forEach(::then) }
        })
    }

    companion object {

        val CODEC: Codec<ArgumentHypixelCommand> = RecordCodecBuilder.create {
            it.group(
                Codec.STRING.fieldOf("name").forGetter(ArgumentHypixelCommand::name),
                Codec.BOOL.optionalFieldOf("greedy", false).forGetter(ArgumentHypixelCommand::greedy),
                HypixelCommandSuggestions.CODEC
                    .optionalFieldOf("suggestions", HypixelCommandSuggestions.NONE)
                    .forGetter(ArgumentHypixelCommand::suggestions),
                HypixelCommand.CODEC.listOf().optionalFieldOf("children", listOf()).forGetter(ArgumentHypixelCommand::children),
            ).apply(it, ::ArgumentHypixelCommand)
        }
    }
}


data class LiteralHypixelCommand(
    val values: List<String>,
    val children: List<HypixelCommand>,
): HypixelCommand {

    override fun toCommand(): List<LiteralArgumentBuilder<FabricClientCommandSource>> {
        return values.map { value ->
            ClientCommandManager.literal(value).apply {
                executes {
                    McClient.self.connection?.send(ServerboundChatCommandPacket(it.input))
                    1
                }
                children.forEach { child -> child.toCommand().forEach(::then) }
            }
        }
    }

    companion object {

        val CODEC: Codec<LiteralHypixelCommand> = Codec.withAlternative(
            RecordCodecBuilder.create {
                it.group(
                    Codec.STRING.listOf().fieldOf("values").forGetter(LiteralHypixelCommand::values),
                    HypixelCommand.CODEC.listOf().optionalFieldOf("children", listOf()).forGetter(LiteralHypixelCommand::children),
                ).apply(it, ::LiteralHypixelCommand)
            },
            Codec.STRING.xmap({ LiteralHypixelCommand(listOf(it), emptyList()) }, { it.values.first() })
        )
    }
}