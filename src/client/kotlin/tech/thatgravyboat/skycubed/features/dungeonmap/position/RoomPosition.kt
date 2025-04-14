package tech.thatgravyboat.skycubed.features.dungeonmap.position

import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonInstance

class RoomPosition(x: Int, y: Int, instance: DungeonInstance) : DungeonPosition<RoomPosition>(x, y, instance) {
    companion object {
        fun from(dungeonPosition: DungeonPosition<*>): RoomPosition {
            if (dungeonPosition is RoomPosition) {
                return dungeonPosition
            }

            with(dungeonPosition.inWorldSpace()) {
                return RoomPosition(convertToRoomIndex(x), convertToRoomIndex(y), instance)
            }
        }

        private fun convertToRoomIndex(value: Int): Int {
            return (Math.round((value + 200) / 32 * 2.0) / 2).toInt()
        }

    }

    override fun inWorldSpace() = WorldPosition(convertFromRoomIndex(x), convertFromRoomIndex(y), instance)
    override val self: RoomPosition get() = this
    override fun copy() = RoomPosition(x, y, instance)

    private fun convertFromRoomIndex(roomIndex: Int): Int {
        return roomIndex * 32 - 200
    }
}