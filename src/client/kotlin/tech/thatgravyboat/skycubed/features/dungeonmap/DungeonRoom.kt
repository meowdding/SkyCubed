package tech.thatgravyboat.skycubed.features.dungeonmap

/**
 * The type of room and the corresponding map color.
 */
enum class DungeonRoomType(val color: Byte) {

    NORMAL(63),
    SPAWN(30),
    PUZZLE(66),
    FAIRY(82),
    TRAP(62),
    MINIBOSS(74),
    BLOOD(18),
    UNKNOWN(85);

    companion object {
        fun byColor(value: Byte): DungeonRoomType {
            return entries.firstOrNull { it.color == value } ?: UNKNOWN
        }
    }

    fun isColor(byte: Byte) = color == byte
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