package tech.thatgravyboat.skycubed.features.overlays.map

import com.mojang.math.Axis
import earth.terrarium.olympus.client.utils.Orientation
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import net.minecraft.client.renderer.RenderType
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.features.dungeonmap.*
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonMap
import tech.thatgravyboat.skycubed.features.dungeonmap.position.RenderPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.RoomPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.combinedSize
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures.backgroundBox

object DungeonMap {

    private val greenCheckmark = SkyCubed.id("map/dungeons/green_checkmark")
    private val whiteCheckmark = SkyCubed.id("map/dungeons/white_checkmark")
    private val cross = SkyCubed.id("map/dungeons/cross")
    private val questionMark = SkyCubed.id("map/dungeons/question_mark")

    val canRender: Boolean get() = OverlaysConfig.dungeonMap.enabled && DungeonFeatures.currentInstance?.map?.cachedMapId != null

    fun render(graphics: GuiGraphics) {
        val instance = DungeonFeatures.currentInstance ?: return
        val map = instance.map ?: return

        graphics.blitSprite(
            RenderType::guiTextured, backgroundBox,
            0, 0,
            90, 90
        )

        graphics.pushPop {
            translate(6f, 6f, 0f)
            scale(0.6f, 0.6f, 1.0f)

            map.doors.forEach { door ->
                map.renderDoor(graphics, door)
            }
            map.roomMap.flatten().distinct().filterNotNull().forEach { room ->
                map.renderRoom(graphics, room)
            }
        }

        instance.players.filterNotNull().forEach { player ->
            val skin = player.getPlayer()?.skin ?: return@forEach
            val pos = player.position.convertTo<RenderPosition>()

            graphics.pushPop {
                translate(6f, 6f, 0f)
                translate((pos.x + 8f) * 0.6f, (pos.y + 8f) * 0.6f, 100f)
                scale(0.8f, 0.8f, 1f)
                rotateAround(Axis.ZP.rotationDegrees(180f + player.rotation.toFloat()), 0f, 0f, 0f)
                translate(-4f, -4f, 100f)
                PlayerFaceRenderer.draw(graphics, skin, 0, 0, 8)
            }
        }
    }

    private fun DungeonMap.renderDoor(graphics: GuiGraphics, door: DungeonDoor) {
        val pos = door.pos.convertTo<RoomPosition>()
        val x = pos.x * (combinedSize + 2)
        val y = pos.y * (combinedSize + 2)

        when (door.orientation) {
            Orientation.VERTICAL -> graphics.color(x + 6, y - 2, combinedSize - 12, 2, door.type.getColor())
            Orientation.HORIZONTAL -> graphics.color(x - 2, y + 6, 2, combinedSize - 12, door.type.getColor())
        }
    }

    private fun DungeonMap.renderRoom(graphics: GuiGraphics, room: DungeonRoom) {
        var iconed = false

        room.positions.map { it.convertTo<RoomPosition>() }.forEach { (x, y) ->
            val realX = x * (combinedSize + 2)
            val realY = y * (combinedSize + 2)
            graphics.color(realX, realY, combinedSize, combinedSize, room.roomType.displayColor)
            if (this.getRoom(x - 1, y) == room) {
                graphics.color(realX - 2, realY, 2, combinedSize, room.roomType.displayColor)
            }
            if (this.getRoom(x, y - 1) == room) {
                graphics.color(realX, realY - 2, combinedSize, 2, room.roomType.displayColor)
            }

            if (iconed) return@forEach

            iconed = true

            val icon = when(room.checkmark) {
                Checkmark.OPENED -> null
                Checkmark.CLEARED -> whiteCheckmark
                Checkmark.FAILED -> cross
                Checkmark.DONE -> greenCheckmark
                Checkmark.UNKNOWN -> questionMark
            }

            graphics.pushPop {
                scale(1 / 0.6f, 1 / 0.6f, 1f)
                icon?.let { location ->
                    val scaledX = (realX + ((combinedSize - 16) / 2)) * 0.6f - 1.5f
                    val scaledY = (realY + ((combinedSize - 16) / 2)) * 0.6f - 2
                    translate(scaledX, scaledY, 0f)
                    scale(0.8f, 0.8f, 1f)
                    graphics.blitSprite(
                        RenderType::guiTextured, location,
                        0, 0,
                        16, 16
                    )
                }
            }
        }
    }

    private fun GuiGraphics.color(x: Int, y: Int, width: Int, height: Int, color: Int) {
        this.fill(x, y, x + width, y + height, color)
    }
}