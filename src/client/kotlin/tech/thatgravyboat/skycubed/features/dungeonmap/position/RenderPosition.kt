package tech.thatgravyboat.skycubed.features.dungeonmap.position

import net.minecraft.util.Mth
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonInstance
import kotlin.math.max
import kotlin.math.roundToInt

const val roomWidth = 20
const val hallwaySize = 4
const val combinedSize = roomWidth + hallwaySize

class RenderPosition(x: Int, y: Int, instance: DungeonInstance) : DungeonPosition<RenderPosition>(x, y, instance) {
    companion object {
        fun from(dungeonPosition: DungeonPosition<*>): RenderPosition {
            if (dungeonPosition is RenderPosition) {
                return dungeonPosition
            }

            with(dungeonPosition.inWorldSpace()) {
                return RenderPosition(
                    fromWorldSpace(
                        this.x,
                        instance.getRoomAmount(),
                        instance.map?.roomsPerAxis?.let { max(it.x, it.y) } ?: 0,
                    ),
                    fromWorldSpace(
                        this.y,
                        instance.getRoomAmount(),
                        instance.map?.roomsPerAxis?.let { max(it.x, it.y) } ?: 0,
                    ),
                    instance,
                )
            }
        }

        private fun fromWorldSpace(value: Int, rooms: Int, max: Int): Int {
            return Mth.clampedMap(
                value.toDouble(),
                -200.0,
                -200 + (rooms * 32.0),
                0.0,
                max * (combinedSize + 2.0) - hallwaySize,
            ).roundToInt() - 2
        }
    }

    override fun inWorldSpace() = WorldPosition(
        convertToWorldSpace(
            this.x,
            instance.getRoomAmount(),
            instance.map?.roomsPerAxis?.let { max(it.x, it.y) } ?: 0,
        ),
        convertToWorldSpace(
            this.y,
            instance.getRoomAmount(),
            instance.map?.roomsPerAxis?.let { max(it.x, it.y) } ?: 0,
        ),
        instance,
    )

    override val self: RenderPosition = this
    override fun copy() = RenderPosition(x, y, instance)

    private fun convertToWorldSpace(value: Int, rooms: Int, max: Int): Int {
        return Mth.clampedMap(
            value.toDouble(),
            0.0,
            (6 * combinedSize) * ((rooms.toDouble()) / max),
            -200.0,
            -200 + (rooms * 32.0),
        ).roundToInt() - 2
    }

}
