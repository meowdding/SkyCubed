package tech.thatgravyboat.skycubed.features.dungeonmap

import org.joml.Vector2i

/**
 * A door in the map, only created once but can have a changing type since doors can be updated
 */
data class DungeonDoor(val pos: Vector2i, val orientation: DungeonDoorOrientation, var type: DungeonDoorType)

/**
 * Rotation of the door, used to be a boolean but that was annoying so its this now
 */
enum class DungeonDoorOrientation {
    LEFT, BOTTOM
}

/**
 * The different types of doors and their respective map color.
 */
enum class DungeonDoorType(val color: Byte) {
    NORMAL(DungeonRoomType.NORMAL.color),
    WITHER(119),
    BLOOD(DungeonRoomType.BLOOD.color),
    PUZZLE(DungeonRoomType.PUZZLE.color),
    TRAP(DungeonRoomType.TRAP.color),
    MINIBOSS(DungeonRoomType.MINIBOSS.color),
    FAIRY(DungeonRoomType.FAIRY.color),
    UNKNOWN(DungeonRoomType.UNKNOWN.color);

    companion object {
        fun getByColor(color: Byte) = entries.firstOrNull { it.color == color }?: UNKNOWN
    }
}