package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import tech.thatgravyboat.lib.displays.Display

interface Poi {

    val id: String

    val enabled: Boolean
        get() = true

    val significant: Boolean
        get() = true

    val tooltip: List<Component>
    val position: Vector2i
    val bounds: Vector2i
    val display: Display

    fun click()

    companion object {

        private val types = mapOf(
            "portal" to PortalPoi.CODEC,
            "npc" to NpcPoi.CODEC,
            "effigy" to EffigyPoi.CODEC,
            "conditional" to ConditionalPoi.CODEC,
        )

        val CODEC: Codec<Poi> = Codec.STRING.partialDispatch(
            "type",
            { DataResult.success(it.id) },
            { type -> types[type]?.let { DataResult.success(it) } ?: DataResult.error { "Unknown type: $type" } }
        )

    }
}