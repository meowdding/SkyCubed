package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import tech.thatgravyboat.skyblockapi.api.area.rift.RiftAPI
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed

data class EffigyPoi(
    private val index: Int
) : Poi {

    override val id: String = "effigy"
    override var position: Vector2i
        get() = RiftAPI.effieges.getOrNull(index)?.pos?.let { Vector2i(it.x, it.z) } ?: Vector2i(0, 0)
        set(_) = throw UnsupportedOperationException()
    override val bounds: Vector2i = Vector2i(6, 6)
    override val display: Display = Displays.sprite(
        SkyCubed.id("map/icons/effigy"),
        6,
        6
    )
    override val enabled: Boolean get() = RiftAPI.effieges.getOrNull(index)?.enabled?.not() ?: false
    override val tooltip: MutableList<Component> = listOf(
        Text.of("Effigy") { color = TextColor.RED },
        CommonText.EMPTY,
//        Text.of("Click to navigate to") {
//            bold = true
//            color = TextColor.YELLOW
//        }
    ).toMutableList()

    override fun click() {
        println(RiftAPI.effieges.getOrNull(index))
    }

    companion object {

        val CODEC: MapCodec<EffigyPoi> = RecordCodecBuilder.mapCodec { it.group(
            Codec.INT.fieldOf("index").forGetter(EffigyPoi::index)
        ).apply(it, ::EffigyPoi) }
    }
}
