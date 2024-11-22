package tech.thatgravyboat.skycubed.features.map

import com.google.gson.JsonElement
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import kotlinx.coroutines.runBlocking
import net.minecraft.core.BlockPos
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import java.util.function.Function

object Maps {

    private val TYPES = arrayOf(
        "main",
        "rift",
        "crystal_hollows",
        "dungeon_hub",
        "jerrys_workshop",
    )

    private val groups: MutableMap<String, List<IslandData>> = mutableMapOf()
    private val islands: MutableMap<SkyBlockIsland, String> = mutableMapOf()

    private var currentIsland: IslandData? = null

    init {
        runBlocking {
            runCatching {
                TYPES.forEach { type ->
                    val file = this.javaClass.getResourceAsStream("/repo/maps/$type.json")?.readJson<JsonElement>() ?: return@runCatching
                    val result = Codec.either(IslandData.CODEC, IslandData.CODEC.listOf())
                        .xmap({ it.map(::listOf, Function.identity()) }, { Either.right(it) })
                        .parse(JsonOps.INSTANCE, file)

                    result.ifError {
                        println("Error parsing maps/$type.json")
                        println(it)
                    }
                    result.ifSuccess { islands ->
                        groups[type] = islands
                        islands.forEach { Maps.islands[it.island] = type }
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @Subscription
    fun onIslandChange(event: IslandChangeEvent) {
        currentIsland = groups[islands[event.new]]?.find { it.island == event.new }
    }

    fun getCurrentOffset(): BlockPos = currentIsland?.let { BlockPos(it.offsetX, 0, it.offsetY) } ?: BlockPos.ZERO

    fun getMapsForLocation(): String = LocationAPI.island?.let(islands::get) ?: "main"

    fun getMaps(map: String?): List<IslandData> = groups[map] ?: emptyList()

    fun getMaps(): List<String> = groups.keys.toList()

}