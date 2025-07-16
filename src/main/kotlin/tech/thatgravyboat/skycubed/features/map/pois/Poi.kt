package tech.thatgravyboat.skycubed.features.map.pois

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import me.owdding.lib.displays.Display
import net.minecraft.network.chat.Component
import org.joml.Vector2i
import org.joml.Vector3i
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skycubed.utils.Rect

interface Poi {

    val id: String

    val enabled: Boolean
        get() = true

    val significant: Boolean
        get() = true

    val tooltip: MutableList<Component>
    var position: Vector3i
    val bounds: Vector2i
    val display: Display

    val rect: Rect get() = Rect(position.x, position.z, bounds.x, bounds.y)

    fun click()

    companion object {

        private val types = mapOf(
            "portal" to PortalPoi.CODEC,
            "npc" to NpcPoi.CODEC,
            "effigy" to EffigyPoi.CODEC,
            "conditional" to ConditionalPoi.CODEC,
        )

        val poiTypes get() = types.keys

        fun createByType(type: String, vector3i: Vector3i): Poi? = when (type) {
            "portal" -> PortalPoi(mutableListOf(), vector3i, "")
            "npc" -> NpcPoi("", "https://wiki.hypixel.net/\$name", "", mutableListOf(), vector3i)
            "effigy" -> EffigyPoi(0)
            else -> {
                Text.of("Can't created type $type").send()
                null
            }
        }

        val CODEC: Codec<Poi> = Codec.STRING.partialDispatch(
            "type",
            { DataResult.success(it.id) },
            { type -> types[type]?.let { DataResult.success(it) } ?: DataResult.error { "Unknown type: $type" } }
        )

    }
}
