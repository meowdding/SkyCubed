package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import tech.thatgravyboat.skyblockapi.api.area.rift.RiftAPI
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.api.displays.Display
import tech.thatgravyboat.skycubed.api.displays.Displays

data class EffigyPoi(
    private val index: Int
) : Poi {

    override val id: String = "effigy"
    override val position: Vector2i get() = RiftAPI.effieges.getOrNull(index)?.pos?.let { Vector2i(it.x, it.z) } ?: Vector2i(0, 0)
    override val bounds: Vector2i = Vector2i(6, 6)
    override val display: Display = Displays.sprite(
        SkyCubed.id("map/icons/effigy"),
        6,
        6
    )
    override val enabled: Boolean get() = RiftAPI.effieges.getOrNull(index)?.enabled?.not() ?: false
    override val tooltip: List<Component> = listOf(
        Text.of("Effigy") { color = TextColor.RED },
        CommonText.EMPTY,
        Text.of("Click to navigate to") {
            bold = true
            color = TextColor.YELLOW
        }
    )

    override fun click() {
        println(RiftAPI.effieges.getOrNull(index))
    }

    companion object {

        val CODEC: MapCodec<EffigyPoi> = RecordCodecBuilder.mapCodec { it.group(
            Codec.INT.fieldOf("index").forGetter(EffigyPoi::index)
        ).apply(it, ::EffigyPoi) }
    }
}