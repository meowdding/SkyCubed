package tech.thatgravyboat.skycubed.features.dungeonmap.position

import net.minecraft.util.Mth
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonInstance
import kotlin.math.roundToInt

class MapPosition(x: Int, y: Int, instance: DungeonInstance) : DungeonPosition<MapPosition>(x, y, instance) {
    companion object {
        fun from(dungeonPosition: DungeonPosition<*>): MapPosition {
            if (dungeonPosition is MapPosition) {
                return dungeonPosition
            }

            with(dungeonPosition.inWorldSpace()) {
                return MapPosition(
                    fromWorldSpace(
                        this.x,
                        instance.map?.mapBox?.minX() ?: 0,
                        instance.map?.mapBox?.maxX() ?: 0,
                        instance.map?.roomsPerAxis?.x ?: 0,
                    ),
                    fromWorldSpace(
                        this.y,
                        instance.map?.mapBox?.minY() ?: 0,
                        instance.map?.mapBox?.maxY() ?: 0,
                        instance.map?.roomsPerAxis?.y ?: 0,
                    ),
                    instance,
                )
            }
        }

        private fun fromWorldSpace(value: Int, topLeft: Int, bottomRight: Int, rooms: Int): Int {
            return Mth.clampedMap(
                value.toDouble(),
                -200.0,
                -200 + (rooms * 32.0),
                topLeft.toDouble(),
                bottomRight.toDouble(),
            ).roundToInt()
        }
    }

    constructor(instance: DungeonInstance) : this(0, 0, instance)

    override fun inWorldSpace() = WorldPosition(
        convertToWorldSpace(
            this.x,
            instance.map?.mapBox?.minX() ?: 0,
            instance.map?.mapBox?.maxX() ?: 0,
            instance.map?.roomsPerAxis?.x ?: 0,
        ),
        convertToWorldSpace(
            this.y,
            instance.map?.mapBox?.minY() ?: 0,
            instance.map?.mapBox?.maxY() ?: 0,
            instance.map?.roomsPerAxis?.y ?: 0,
        ),
        instance,
    )

    override val self: MapPosition get() = this
    override fun copy() = MapPosition(x, y, instance)

    private fun convertToWorldSpace(value: Int, topLeft: Int, bottomRight: Int, rooms: Int): Int {
        return Mth.clampedMap(
            value.toDouble(),
            topLeft.toDouble(),
            bottomRight.toDouble() + hallwaySize,
            -200.0,
            -200 + (rooms * 32.0),
        ).roundToInt()
    }

}
