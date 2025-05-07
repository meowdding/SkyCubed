package tech.thatgravyboat.skycubed.features.dungeonmap

/**
 * A checkmark on the dungeon map, failed is only applied to puzzle room types.
 */
enum class Checkmark(val color: Byte) {
    UNKNOWN(119),
    OPENED(-1),
    CLEARED(34),
    FAILED(18),
    DONE(30);

    companion object {
        fun getByColor(color: Byte) = entries.firstOrNull { it.color == color }
    }
}

enum class DungeonPhase {
     BEFORE, CLEAR, BLOOD, BOSS, AFTER
}