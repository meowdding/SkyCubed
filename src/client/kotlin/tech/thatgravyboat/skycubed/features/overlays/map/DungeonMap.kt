package tech.thatgravyboat.skycubed.features.overlays.map

import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.utils.Orientation
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.Mth
import net.minecraft.world.level.material.MapColor
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonFeatures
import tech.thatgravyboat.skycubed.features.dungeonmap.position.DungeonPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.RenderPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.RoomPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.combinedSize

object DungeonMap {

    val canRender: Boolean get() = true

    fun render(graphics: GuiGraphics) {
        val instance = DungeonFeatures.currentInstance ?: return
        val dungeonMap = instance.map ?: return

        graphics.fill(0, 0, 26 * 5, 26 * 5, -1)

        val rooms = dungeonMap.roomMap.flatten().distinct().filterNotNull()
        rooms.forEach { room ->
            val positions = room.positions.map { it.convertTo<RoomPosition>() }

            positions.forEach { (x, y) ->
                graphics.fill(
                    ((combinedSize + 2) * x),
                    ((combinedSize + 2) * y),
                    ((combinedSize + 2) * x) + combinedSize,
                    ((combinedSize + 2) * y) + combinedSize,
                    0xFF000000u.toInt().or(room.hashCode())
                )
            }
        }

        dungeonMap.doors.forEach { door ->
            val pos = door.pos.convertTo<RoomPosition>()
            val x = when (door.orientation) {
                Orientation.VERTICAL -> pos.x * (combinedSize + 2)
                Orientation.HORIZONTAL -> pos.x * (combinedSize + 2) - 2
            }
            val y = when (door.orientation) {
                Orientation.VERTICAL -> pos.y * (combinedSize + 2) - 2
                Orientation.HORIZONTAL -> pos.y * (combinedSize + 2)
            }

            graphics.fill(
                x,
                y,
                x + (if (door.orientation == Orientation.VERTICAL) combinedSize else 2),
                y + (if (door.orientation == Orientation.VERTICAL) 2 else combinedSize),
                MapColor.getColorFromPackedId(door.type.color.toInt())
            )
        }

        instance.players.filterNotNull().forEach { player ->
            val renderPosition = player.position.convertTo<RenderPosition>()
            graphics.run {
                fill(
                        renderPosition.x,
                        renderPosition.y,
                        renderPosition.x + 10,
                        renderPosition.y + 10,
                        if (player.isSelf) -1 else player.hashCode().or(0xFF000000u.toInt())
                    )
            }

            graphics.drawString(McFont.self,
                "${player.position.x()} ${player.position.y()}",
                renderPosition.x - 5,
                renderPosition.y - 12,
                MinecraftColors.RED.value
            )
        }
    }

}