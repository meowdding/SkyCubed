package tech.thatgravyboat.skycubed.features.overlays.rpg

import com.google.gson.JsonElement
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.skycubed.generated.SkyCubedCodecs
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.joml.Vector2i
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.gson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skycubed.SkyCubed
import kotlin.jvm.optionals.getOrNull

@Module
object RpgOverlayPositionHandler : SimplePreparableReloadListener<RpgOverlayPositionHandler.RpgOverlayPosition>() {

    private val id = SkyCubed.id("textures/gui/sprites/rpg/positions.json")

    init {
        McClient.registerClientReloadListener(id, this)
    }

    var positions: RpgOverlayPosition = RpgOverlayPosition.DEFAULT
        private set


    override fun prepare(manager: ResourceManager, profiler: ProfilerFiller): RpgOverlayPosition {
        return manager.getResource(id).getOrNull().let {
            it?.openAsReader().use { reader ->
                gson.fromJson(reader, JsonElement::class.java).toDataOrThrow(SkyCubedCodecs.getCodec<RpgOverlayPosition>())
            }
        }
    }

    override fun apply(modifiers: RpgOverlayPosition, manager: ResourceManager, profiler: ProfilerFiller) {
        this.positions = modifiers
    }

    @GenerateCodec
    data class RpgOverlayPosition(
        val mana: Vector2i,
        val health: Vector2i,
        val xpBar: Vector2i,
        val xpText: Vector2i,
        val airBase: Vector2i,
        var airBar: Vector2i,
    ) {
        companion object {
            val DEFAULT = RpgOverlayPosition(
                mana = Vector2i(47, 18),
                health = Vector2i(47, 23),
                xpBar = Vector2i(47, 29),
                xpText = Vector2i(3, 33),
                airBase = Vector2i(38, 34),
                airBar = Vector2i(40, 34),
            )
        }
    }
}
