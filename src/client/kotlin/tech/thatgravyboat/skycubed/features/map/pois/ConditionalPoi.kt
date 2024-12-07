package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import tech.thatgravyboat.skycubed.api.conditions.Condition
import tech.thatgravyboat.skycubed.api.displays.Display

class ConditionalPoi(
    val enabledCondition: Condition,
    val significanceCondition: Condition,
    val poi: Poi
) : Poi {

    override val id: String = "conditional"
    override val tooltip: List<Component> get() = poi.tooltip
    override val position: Vector2i get() = poi.position
    override val bounds: Vector2i get() = poi.bounds
    override val display: Display get() = poi.display
    override val enabled: Boolean get() = enabledCondition.test() && poi.enabled
    override val significant: Boolean get() = significanceCondition.test() && poi.significant

    override fun click() {
        poi.click()
    }

    companion object {

        val CODEC: MapCodec<ConditionalPoi> = RecordCodecBuilder.mapCodec { it.group(
            Condition.CODEC.optionalFieldOf("enabled", Condition.TRUE).forGetter(ConditionalPoi::enabledCondition),
            Condition.CODEC.optionalFieldOf("significant", Condition.TRUE).forGetter(ConditionalPoi::significanceCondition),
            Codec.lazyInitialized { Poi.CODEC }.fieldOf("poi").forGetter(ConditionalPoi::poi)
        ).apply(it, ::ConditionalPoi) }
    }
}