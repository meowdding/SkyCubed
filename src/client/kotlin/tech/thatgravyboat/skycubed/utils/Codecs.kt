package tech.thatgravyboat.skycubed.utils

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.joml.Vector2i
import java.util.function.Function

object Codecs {

    fun vec2i(first: String, second: String): Codec<Vector2i> {
        return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Vector2i> ->
            instance.group(
                Codec.INT.fieldOf(first).forGetter { obj: Vector2i -> obj.x },
                Codec.INT.fieldOf(second).forGetter { obj: Vector2i -> obj.y }
            ).apply(instance, ::Vector2i)
        }
    }

    fun <A> Codec<A>.asMapCodec(key: String): MapCodec<A> {
        return RecordCodecBuilder.mapCodec { it.group(
            this.fieldOf(key).forGetter(Function.identity())
        ).apply(it, Function.identity()) }
    }
}