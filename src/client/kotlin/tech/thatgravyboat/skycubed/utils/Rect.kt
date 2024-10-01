package tech.thatgravyboat.skycubed.utils

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
}
