package tech.thatgravyboat.skycubed.features.dungeonmap

import earth.terrarium.olympus.client.utils.Orientation
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonDoorType.entries
import tech.thatgravyboat.skycubed.features.dungeonmap.position.DungeonPosition

/**
 * A door in the map, only created once but can have a changing type since doors can be updated.
 *
 * @param orientation horizontal = left; vertical = top
 */
data class DungeonDoor(val pos: DungeonPosition<*>, val orientation: Orientation, var type: DungeonDoorType) {
    fun isAt(orientation: Orientation, mapPosition: DungeonPosition<*>): Boolean {
        return pos.inWorldSpace() == mapPosition.inWorldSpace() && this.orientation == orientation
    }
}

/**
 * The different types of doors and their respective map color.
 */
enum class DungeonDoorType(val color: Byte, val defaultDisplayColor: Int) {
    NORMAL(DungeonRoomType.NORMAL.color, DungeonRoomType.NORMAL.defaultDisplayColor),
    WITHER(119, 0x0D0D0D),
    BLOOD(DungeonRoomType.BLOOD.color, DungeonRoomType.BLOOD.defaultDisplayColor),
    PUZZLE(DungeonRoomType.PUZZLE.color, DungeonRoomType.PUZZLE.defaultDisplayColor),
    TRAP(DungeonRoomType.TRAP.color, DungeonRoomType.TRAP.defaultDisplayColor),
    MINIBOSS(DungeonRoomType.MINIBOSS.color, DungeonRoomType.MINIBOSS.defaultDisplayColor),
    FAIRY(DungeonRoomType.FAIRY.color, DungeonRoomType.FAIRY.defaultDisplayColor),
    UNKNOWN(DungeonRoomType.UNKNOWN.color, DungeonRoomType.UNKNOWN.defaultDisplayColor),
    ;

    var displayColor: Int = defaultDisplayColor

    companion object {
        fun getByColor(color: Byte) = entries.firstOrNull { it.color == color }
    }
}
