package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.ktcodecs.FieldName
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.rendering.text.serialization.TagComponentSerialization
import me.owdding.skycubed.generated.CodecUtils
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import org.joml.Vector2i
import org.joml.Vector3i
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.utils.CachedValue
import tech.thatgravyboat.skycubed.utils.Codecs
import kotlin.time.Duration.Companion.seconds

data class PortalPoi(
    @FieldName("tooltip") var stringTooltip: MutableList<String>,
    override var position: Vector3i,
    var destination: String,
) : Poi {
    override val tooltip: MutableList<Component>  by CachedValue(5.seconds) {
        stringTooltip.map { TagComponentSerialization.deserialize(it) }.toMutableList()
    }

    override val id: String = "portal"
    override val bounds: Vector2i = Vector2i(16, 16)
    override val display: Display = Displays.sprite(
        SkyCubed.id("map/icons/portal"),
        16,
        16,
    )

    override fun click() {
        McClient.sendCommand("warp $destination")
        McClient.setScreen(null)
    }

    companion object {

        val CODEC: MapCodec<PortalPoi> = RecordCodecBuilder.mapCodec {
            it.group(
                CodecUtils.list(Codec.STRING).optionalFieldOf("tooltip", mutableListOf()).forGetter(PortalPoi::stringTooltip),
                Codecs.vec3i("x", "y", "z").fieldOf("pos").forGetter(PortalPoi::position),
                Codec.STRING.fieldOf("destination").forGetter(PortalPoi::destination),
            ).apply(it) { t, p, d -> PortalPoi(t.toMutableList(), p, d) }
        }
    }
}
