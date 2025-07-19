package tech.thatgravyboat.skycubed.features.commands.hypixel

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import tech.thatgravyboat.skyblockapi.api.profile.friends.FriendsAPI
import tech.thatgravyboat.skyblockapi.api.profile.party.PartyAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient

typealias HypixelCommandSuggestion = () -> List<String>

object HypixelCommandSuggestions {

    private val friends: HypixelCommandSuggestion = {
        FriendsAPI.friends
            .map { it.name }
    }

    private val bestFriends: HypixelCommandSuggestion = {
        FriendsAPI.friends
            .filter { it.bestFriend }
            .map { it.name }
    }

    private val partyMember: HypixelCommandSuggestion = {
        PartyAPI.members
            .mapNotNull { it.name }
    }

    private val lobby: HypixelCommandSuggestion = {
        McClient.players.map { it.profile.name }
    }

    private val players: HypixelCommandSuggestion = {
        (friends() + partyMember() + lobby()).distinct()
    }

    val NONE: HypixelCommandSuggestion = { listOf() }

    val CODEC = Codec.either(Codec.STRING.listOf(), Codec.STRING)
        .comapFlatMap(
            {
                it.map(
                    { DataResult.success(it.asSuggestion()) },
                    { id ->
                        when (id) {
                            "friends" -> DataResult.success(friends)
                            "bestfriends" -> DataResult.success(bestFriends)
                            "party" -> DataResult.success(partyMember)
                            "players" -> DataResult.success(players)
                            else -> DataResult.error { "Cannot find suggestion provider with name $id" }
                        }
                    }
                )
            },
            {
                when (it) {
                    friends -> Either.right("friends")
                    bestFriends -> Either.right("bestfriends")
                    partyMember -> Either.right("party")
                    players -> Either.right("players")
                    else -> Either.left(it.invoke())
                }
            }
        )

    private fun List<String>.asSuggestion(): HypixelCommandSuggestion = {
        this
    }
}