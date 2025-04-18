package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import org.joml.Vector2i
import tech.thatgravyboat.lib.displays.Display
import tech.thatgravyboat.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.utils.Codecs

data class PortalPoi(
    override val tooltip: List<Component>,
    override val position: Vector2i,
    val destination: String
) : Poi {

    override val id: String = "portal"
    override val bounds: Vector2i = Vector2i(16, 16)
    override val display: Display = Displays.sprite(
        SkyCubed.id("map/icons/portal"),
        16,
        16
    )

    override fun click() = McClient.sendCommand("warp $destination")

    companion object {

        val CODEC: MapCodec<PortalPoi> = RecordCodecBuilder.mapCodec { it.group(
            ComponentSerialization.CODEC.listOf().optionalFieldOf("tooltip", listOf()).forGetter(PortalPoi::tooltip),
            Codecs.vec2i("x", "z").fieldOf("pos").forGetter(PortalPoi::position),
            Codec.STRING.fieldOf("destination").forGetter(PortalPoi::destination)
        ).apply(it, ::PortalPoi) }
    }
}