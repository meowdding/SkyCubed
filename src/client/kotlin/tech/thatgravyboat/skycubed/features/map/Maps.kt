package tech.thatgravyboat.skycubed.features.map

import com.google.gson.JsonElement
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.teamresourceful.resourcefullib.common.lib.Constants
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.core.BlockPos
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.features.map.screen.MapScreen
import tech.thatgravyboat.skycubed.utils.readJsonc
import java.util.function.Function

object Maps {

    private val TYPES = arrayOf(
        "main",
        "rift",
        "crystal_hollows",
        "dwarves",
        "dungeon_hub",
        "jerrys_workshop",
    )
    private val KEYBIND = KeyBindingHelper.registerKeyBinding(KeyMapping("key.skycubed.map", InputConstants.KEY_M, "key.skycubed.category"))

    private val groups: MutableMap<String, List<IslandData>> = mutableMapOf()
    private val islands: MutableMap<SkyBlockIsland, String> = mutableMapOf()

    private var currentIsland: IslandData? = null

    init {
        runBlocking {
            runCatching {
                TYPES.forEach { type ->
                    val file = this.javaClass.getResourceAsStream("/repo/maps/$type.jsonc")?.readJsonc<JsonElement>() ?: return@runCatching
                    val result = Codec.either(IslandData.CODEC, IslandData.CODEC.listOf())
                        .xmap({ it.map(::listOf, Function.identity()) }, { Either.right(it) })
                        .parse(JsonOps.INSTANCE, file)

                    result.ifError {
                        Constants.LOGGER.error("Error parsing maps/$type.json, error: {}", it)
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

    @Subscription
    fun onTick(event: TickEvent) {
        if (KEYBIND.consumeClick()) {
            McClient.setScreen(MapScreen())
        }
    }

    fun getCurrentOffset(): BlockPos = currentIsland?.let {
        BlockPos(it.offsetX, 0, it.offsetY)
    } ?: BlockPos.ZERO

    fun getCurrentPlayerOffset(): BlockPos = currentIsland?.let {
        BlockPos(it.offsetX + it.playerOffsetX, 0, it.offsetY + it.playerOffsetY)
    } ?: BlockPos.ZERO

    fun getMapsForLocationOrNull(): String? = LocationAPI.island?.let(islands::get)

    fun getMapsForLocation(): String = getMapsForLocationOrNull() ?: "main"

    fun getMaps(map: String?): List<IslandData> = groups[map] ?: emptyList()

    fun getMaps(): List<String> = groups.keys.toList()

}