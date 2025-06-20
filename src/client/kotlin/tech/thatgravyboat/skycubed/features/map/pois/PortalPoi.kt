package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.skycubed.generated.CodecUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import org.joml.Vector2i
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.utils.Codecs

data class PortalPoi(
    override val tooltip: MutableList<Component>,
    override var position: Vector2i,
    var destination: String,
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
            CodecUtils.list(ComponentSerialization.CODEC).optionalFieldOf("tooltip", mutableListOf()).forGetter(PortalPoi::tooltip),
            Codecs.vec2i("x", "z").fieldOf("pos").forGetter(PortalPoi::position),
            Codec.STRING.fieldOf("destination").forGetter(PortalPoi::destination),
        ).apply(it, ::PortalPoi) }
    }
}
