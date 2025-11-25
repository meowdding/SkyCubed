package tech.thatgravyboat.skycubed.features.overlays.rpg

import com.google.gson.JsonElement
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.skycubed.generated.SkyCubedCodecs
import net.minecraft.client.gui.navigation.ScreenDirection
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

    var positions: RpgOverlayPosition = RpgOverlayPosition()
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
        val player: RpgOverlayPlayer = RpgOverlayPlayer(),
        val base: RpgOverlayBase = RpgOverlayBase(119, 48),
        val mana: RpgOverlayElement = RpgOverlayElement(47, 18, 57, 4),
        val health: RpgOverlayElement = RpgOverlayElement(47, 23, 70, 5),
        val xpBar: RpgOverlayElement = RpgOverlayElement(47, 29, 67, 4),
        val xpText: Vector2i = Vector2i(3, 33),
        val airBase: RpgOverlayElement = RpgOverlayElement(38, 34, 64, 6),
        var airBar: RpgOverlayElement = RpgOverlayElement(40, 34, 60, 4),
    )

    @GenerateCodec
    data class RpgOverlayElement(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int,
        val direction: ScreenDirection = ScreenDirection.RIGHT,
    )

    @GenerateCodec
    data class RpgOverlayBase(
        val width: Int,
        val height: Int,
    )

    @GenerateCodec
    data class RpgOverlayPlayer(
        val x: Int = 0,
        val y: Int = 0,
        val width: Int = 48,
        val height: Int = 48,
        val xRot: Float = -15f,
        val yRot: Float = 0f,
        val scale: Float = 30f,
    )
}
