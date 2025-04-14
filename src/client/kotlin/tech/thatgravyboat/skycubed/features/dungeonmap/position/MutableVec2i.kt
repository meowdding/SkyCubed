package tech.thatgravyboat.skycubed.features.dungeonmap.position

import org.joml.Vector2i

open class MutableVec2i(x: Int, y: Int) : Vector2i(x, y) {
    constructor(value: Int) : this(value, value)
    constructor() : this(0)
}