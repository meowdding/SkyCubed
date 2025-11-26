package tech.thatgravyboat.skycubed.utils

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.ktcodecs.IncludedCodec
import org.joml.Vector2i
import org.joml.Vector2ic
import org.joml.Vector3i
import java.util.function.Function

object Codecs {

    fun vec3i(first: String, second: String, third: String): Codec<Vector3i> {
        return RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Vector3i> ->
            instance.group(
                Codec.INT.fieldOf(first).forGetter { obj: Vector3i -> obj.x },
                Codec.INT.fieldOf(second).forGetter { obj: Vector3i -> obj.y },
                Codec.INT.fieldOf(third).forGetter { obj: Vector3i -> obj.z }
            ).apply(instance, ::Vector3i)
        }
    }

    fun <A> Codec<A>.asMapCodec(key: String): MapCodec<A> {
        return RecordCodecBuilder.mapCodec { it.group(
            this.fieldOf(key).forGetter(Function.identity())
        ).apply(it, Function.identity()) }
    }

    @IncludedCodec
    val VEC3I: Codec<Vector3i> = vec3i("x", "y", "z")

    @IncludedCodec
    val vec2iCodec: Codec<Vector2i> = RecordCodecBuilder.create {
        it.group(
            Codec.INT.fieldOf("x").forGetter(Vector2ic::x),
            Codec.INT.fieldOf("y").forGetter(Vector2ic::y),
        ).apply(it, ::Vector2i)
    }
}
