package tech.thatgravyboat.skycubed.features.overlays.rpg

import com.google.gson.JsonElement
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.skycubed.generated.SkyCubedCodecs
import net.minecraft.core.Direction
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
        val base: RpgOverlayBase,
        val mana: RpgOverlayElement,
        val health: RpgOverlayElement,
        val xpBar: RpgOverlayElement,
        val xpText: Vector2i,
        val airBase: RpgOverlayElement,
        var airBar: RpgOverlayElement,
    ) {
        companion object {
            val DEFAULT = RpgOverlayPosition(
                base = RpgOverlayBase(119, 48),
                mana = RpgOverlayElement(47, 18, 57, 4),
                health = RpgOverlayElement(47, 23, 70, 5),
                xpBar = RpgOverlayElement(47, 29, 67, 4),
                xpText = Vector2i(3, 33),
                airBase = RpgOverlayElement(38, 34, 64, 6),
                airBar = RpgOverlayElement(40, 34, 60, 4),
            )
        }
    }

    @GenerateCodec
    data class RpgOverlayElement(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int,
        val direction: Direction = Direction.EAST,
    )

    @GenerateCodec
    data class RpgOverlayBase(
        val width: Int,
        val height: Int,
    )
}
