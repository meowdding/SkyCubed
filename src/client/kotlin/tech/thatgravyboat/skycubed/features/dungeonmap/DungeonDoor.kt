package tech.thatgravyboat.skycubed.features.dungeonmap

import earth.terrarium.olympus.client.utils.Orientation
import net.minecraft.world.level.material.MapColor
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
enum class DungeonDoorType(val color: Byte) {
    NORMAL(DungeonRoomType.NORMAL.color),
    WITHER(119),
    BLOOD(DungeonRoomType.BLOOD.color),
    PUZZLE(DungeonRoomType.PUZZLE.color),
    TRAP(DungeonRoomType.TRAP.color),
    MINIBOSS(DungeonRoomType.MINIBOSS.color),
    FAIRY(DungeonRoomType.FAIRY.color),
    UNKNOWN(DungeonRoomType.UNKNOWN.color);

    fun getColor() = MapColor.getColorFromPackedId(color.toInt())

    companion object {
        fun getByColor(color: Byte) = entries.firstOrNull { it.color == color }
    }
}