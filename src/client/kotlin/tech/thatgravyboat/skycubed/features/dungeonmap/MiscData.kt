package tech.thatgravyboat.skycubed.features.dungeonmap

/**
 * The different types of dungeons currently supported.
 */
enum class DungeonType {
    CATACOMBS, CATACOMBS_MASTERMODE, NONE;

    fun isCatacombs() = this == CATACOMBS || this == CATACOMBS_MASTERMODE
    fun isMastermode() = this == CATACOMBS_MASTERMODE
}

// Todo textures
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
        fun getByColor(color: Byte) = entries.firstOrNull { it.color == color } ?: UNKNOWN
    }
}