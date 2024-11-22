package tech.thatgravyboat.skycubed.utils

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class Rect(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int
) {

    val right: Int
        get() = x + width

    val bottom: Int
        get() = y + height

    fun contains(x: Number, y: Number): Boolean {
        return x.toInt() in this.x until right && y.toInt() in this.y until bottom
    }

    operator fun times(scale: Float): Rect {
        return Rect(x, y, (width * scale).toInt(), (height * scale).toInt())
    }

    companion object {

        val CODEC: Codec<Rect> = RecordCodecBuilder.create { it.group(
            Codec.INT.fieldOf("x").forGetter(Rect::x),
            Codec.INT.fieldOf("y").forGetter(Rect::y),
            Codec.INT.fieldOf("width").forGetter(Rect::width),
            Codec.INT.fieldOf("height").forGetter(Rect::height)
        ).apply(it, ::Rect) }

        fun codec(width: Int, height: Int): Codec<Rect> = RecordCodecBuilder.create { it.group(
            Codec.INT.fieldOf("x").forGetter(Rect::x),
            Codec.INT.fieldOf("y").forGetter(Rect::y)
        ).apply(it) { x, y -> Rect(x, y, width, height) } }
    }
}
