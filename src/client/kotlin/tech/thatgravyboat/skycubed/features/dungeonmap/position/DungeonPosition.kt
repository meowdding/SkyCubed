package tech.thatgravyboat.skycubed.features.dungeonmap.position

import org.joml.Vector2i
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonInstance

abstract class DungeonPosition<T: DungeonPosition<T>>(x: Int, y: Int, val instance: DungeonInstance) : Vector2i(x, y) {

    inline fun <reified T : DungeonPosition<T>> convertTo(): T {
        return when (T::class) {
            MapPosition::class -> MapPosition.from(this) as T
            WorldPosition::class -> WorldPosition.from(this) as T
            RoomPosition::class -> RoomPosition.from(this) as T
            RenderPosition::class -> RenderPosition.from(this) as T
            else -> throw UnsupportedOperationException("Unknown type, T: ${T::class}")
        }
    }

    abstract fun inWorldSpace(): WorldPosition

    fun plus(x: Int, y: Int): T {
        return this.copy().apply { add(x,y) }
    }

    fun subtract(x: Int, y: Int): T {
        return this.copy().apply { sub(x,y) }
    }

    protected abstract val self: T
    abstract fun copy(): T

    operator fun component1() = x
    operator fun component2() = y
}
