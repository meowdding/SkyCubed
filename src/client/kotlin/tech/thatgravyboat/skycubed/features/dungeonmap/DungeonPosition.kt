package tech.thatgravyboat.skycubed.features.dungeonmap

import net.minecraft.util.Mth
import kotlin.math.roundToInt

class DungeonPosition {
    var worldX: Int
    var worldY: Int
    var instance: DungeonInstance

    constructor(worldX: Int, worldY: Int) {
        this.worldX = worldX
        this.worldY = worldY
        instance = DungeonInstance()
    }

    fun convertFromRoomIndex(roomIndex: Int): Int {
        return roomIndex * 32 - 200
    }

    fun convertToRoomIndex(value: Int): Int {
        return (Math.round((value + 200) / 32 * 2.0) / 2).toInt()
    }

    fun convertMapSpaceToWorldSpace(value: Int, topLeft: Int, bottomRight: Int, rooms: Int): Int {
        return Mth.clampedMap(
            value.toDouble(),
            topLeft.toDouble(),
            bottomRight.toDouble(),
            -200.0,
            -200 + (rooms * 32.0)
        ).roundToInt()
    }

    fun convertWorldSpaceToMapSpace(value: Int, topLeft: Int, bottomRight: Int, rooms: Int): Int {
        return Mth.clampedMap(
            value.toDouble(),
            -200.0,
            -200 + (rooms * 32.0),
            topLeft.toDouble(),
            bottomRight.toDouble()
        ).roundToInt()
    }

}
