package tech.thatgravyboat.skycubed.features.overlays.map

import earth.terrarium.olympus.client.utils.Orientation
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import tech.thatgravyboat.skyblockapi.platform.*
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.config.overlays.DungeonMapOverlayConfig
import tech.thatgravyboat.skycubed.features.dungeonmap.*
import tech.thatgravyboat.skycubed.features.dungeonmap.position.RenderPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.RoomPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.combinedSize
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures.backgroundBox

@Module
object DungeonMapOverlay {

    private val greenCheckmark = SkyCubed.id("map/dungeons/green_checkmark")
    private val whiteCheckmark = SkyCubed.id("map/dungeons/white_checkmark")
    private val cross = SkyCubed.id("map/dungeons/cross")
    private val questionMark = SkyCubed.id("map/dungeons/question_mark")

    val canRender: Boolean get() = DungeonMapOverlayConfig.enabled && DungeonFeatures.currentInstance?.map?.cachedMapId != null

    fun render(graphics: GuiGraphics) {
        val instance = DungeonFeatures.currentInstance ?: return
        val map = instance.map ?: return

        graphics.enableScissor(0, 0, 90, 90)

        graphics.drawSprite(backgroundBox, 0, 0, 90, 90)

        val width = instance.getRoomAmount() * (combinedSize + 2)
        val scaleFactor = 78 / width.toFloat()
        graphics.pushPop {
            graphics.translate(6f, 6f)
            graphics.scale(scaleFactor, scaleFactor)

            instance.runCatching {
                map.doors.forEach { door ->
                    map.renderDoor(graphics, door)
                }
                map.roomMap.flatten().distinct().filterNotNull().forEach { room ->
                    map.renderRoom(graphics, room)
                }
            }
        }

        instance.players.filterNotNull().forEach { player ->
            val skin = player.getPlayer()?.skin ?: return@forEach
            val pos = instance.runCatching<RenderPosition> { player.position.convertTo<RenderPosition>() } ?: return@forEach

            graphics.pushPop {
                graphics.translate(6f, 6f)
                graphics.translate((pos.x + 8f) * scaleFactor, (pos.y + 8f) * scaleFactor)
                graphics.rotate(180f + player.rotation.toFloat())
                graphics.translate(-4f, -4f)
                PlayerFaceRenderer.draw(graphics, skin, 0, 0, 8)
            }
        }

        graphics.disableScissor()
    }

    private fun DungeonMap.renderDoor(graphics: GuiGraphics, door: DungeonDoor) {
        val pos = door.pos.convertTo<RoomPosition>()
        val x = pos.x * (combinedSize + 2)
        val y = pos.y * (combinedSize + 2)

        when (door.orientation) {
            Orientation.VERTICAL -> graphics.color(x + 6, y - 2, combinedSize - 12, 2, door.type.displayColor)
            Orientation.HORIZONTAL -> graphics.color(x - 2, y + 6, 2, combinedSize - 12, door.type.displayColor)
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

            val icon = when (room.checkmark) {
                Checkmark.OPENED -> null
                Checkmark.CLEARED -> whiteCheckmark
                Checkmark.FAILED -> cross
                Checkmark.DONE -> greenCheckmark
                Checkmark.UNKNOWN -> questionMark
            }

            graphics.pushPop {
                graphics.scale(1 / 0.6f, 1 / 0.6f)
                icon?.let { location ->
                    val scaledX = (realX + ((combinedSize - 16) / 2)) * 0.6f - 1.5f
                    val scaledY = (realY + ((combinedSize - 16) / 2)) * 0.6f - 2
                    graphics.translate(scaledX, scaledY)
                    graphics.scale(0.8f, 0.8f)
                    graphics.drawSprite(location, 0, 0, 16, 16)
                }
            }
        }
    }

    private fun GuiGraphics.color(x: Int, y: Int, width: Int, height: Int, color: Int) {
        this.fill(x, y, x + width, y + height, color.or(0xFF000000.toInt()))
    }
}
