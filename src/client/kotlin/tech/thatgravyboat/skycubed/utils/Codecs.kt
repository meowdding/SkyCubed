package tech.thatgravyboat.skycubed.utils

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.joml.Vector2i

object Codecs {

    fun vec2i(first: String, second: String): Codec<Vector2i> {
        return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Vector2i> ->
            instance.group(
                Codec.INT.fieldOf(first).forGetter { obj: Vector2i -> obj.x },
                Codec.INT.fieldOf(second).forGetter { obj: Vector2i -> obj.y }
            ).apply(instance, ::Vector2i)
        }
    }
}