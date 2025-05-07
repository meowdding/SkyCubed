package tech.thatgravyboat.skycubed.features.dungeonmap

import net.minecraft.world.level.material.MapColor
import tech.thatgravyboat.skycubed.features.dungeonmap.position.DungeonPosition

class DungeonRoom(val instance: DungeonInstance, var roomType: DungeonRoomType, val positions: MutableList<DungeonPosition<*>> = mutableListOf()) {
    var mergedWith: DungeonRoom? = null
    var checkmark: Checkmark = Checkmark.OPENED
        set(value) {
            if (!this.roomType.isAllowed(value)) return
            field = value
        }
    var puzzleDirty = false
    var puzzleId = -1
}

/**
 * The type of room and the corresponding map color.
 */
enum class DungeonRoomType(val color: Byte, private val _displayColor: Int) {

    NORMAL(63, MapColor.DIRT.col),
    SPAWN(30, MapColor.COLOR_GREEN.col),
    PUZZLE(66, MapColor.COLOR_PURPLE.col),
    FAIRY(82, MapColor.COLOR_PINK.col),
    BLOOD(18, MapColor.COLOR_RED.col),
    TRAP(62, MapColor.COLOR_ORANGE.col),
    MINIBOSS(74, MapColor.COLOR_YELLOW.col),
    UNKNOWN(85, MapColor.COLOR_GRAY.col);

    val displayColor: Int get() = 0xFF000000u.toInt().or(this._displayColor)

    fun isColor(byte: Byte) = color == byte

    fun isAllowed(checkmark: Checkmark): Boolean = when (checkmark) {
        Checkmark.DONE -> this != SPAWN
        Checkmark.FAILED -> this == PUZZLE
        Checkmark.UNKNOWN -> this == UNKNOWN
        else -> true
    }

    companion object {
        fun getByColor(value: Byte): DungeonRoomType? {
            return entries.firstOrNull { it.color == value }
        }
    }
}

/**
 * All types of puzzles in the catacombs
 */
enum class PuzzleType(val tabName: String, val displayName: String = tabName) {
    QUIZ("Quiz"),
    TIC_TAC_TOE("Tic Tac Toe"),
    WEIRDOS("Three Weirdos"),
    ICE_PATH("Ice Path"),
    ICE_FILL("Ice Fill"),
    HIGHER_LOWER("Higher Or Lower", "Higher Lower"),
    CREEPER("Creeper Beams", "Creeper"),
    WATERBOARD("Water Board"),
    BOULDER("Boulder"),
    MAZE("Teleport Maze"),
    UNKNOWN("Unknown");

    companion object {
        fun byTabName(tabName: String): PuzzleType {
            return entries.firstOrNull { it.tabName == tabName } ?: UNKNOWN
        }
    }
}