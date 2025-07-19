package tech.thatgravyboat.skycubed.features.dungeonmap.position

import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonInstance

class WorldPosition(x: Int, y: Int, instance: DungeonInstance) : DungeonPosition<WorldPosition>(x, y, instance) {
    companion object {
        fun from(dungeonPosition: DungeonPosition<*>): WorldPosition {
            if (dungeonPosition is WorldPosition) {
                return dungeonPosition
            }

            return dungeonPosition.inWorldSpace()
        }
    }

    override fun inWorldSpace() = this
    override val self: WorldPosition get() = this
    override fun copy() = WorldPosition(x, y, instance)
}