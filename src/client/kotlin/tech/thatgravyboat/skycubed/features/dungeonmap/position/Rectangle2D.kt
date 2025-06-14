package tech.thatgravyboat.skycubed.features.dungeonmap.position

import org.joml.Vector2i
import kotlin.math.abs

data class Rectangle2D(var top: Int, var left: Int, var right: Int, var bottom: Int) {
    constructor() : this(0, 0, 0, 0)

    fun includes(point: Vector2i) = includes(point.x, point.y)

    fun includes(x: Int, y: Int): Boolean {
        return x in this.left..this.right && y in this.top..this.bottom
    }

    fun minX() = this.left
    fun maxX() = this.right
    fun minY() = this.top
    fun maxY() = this.bottom

    fun topLeft() = Vector2i(this.top, this.left)
    fun bottomRight() = Vector2i(this.bottom, this.right)
    fun size() = abs((bottom - top) * (right - left))
    fun isDefined() = size() != 0
}
