package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.platform.InputConstants
import com.teamresourceful.resourcefullib.client.screens.CursorScreen.Cursor
import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.olympus.client.utils.State
import me.owdding.lib.waypoints.MeowddingWaypoint
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import net.minecraft.client.gui.screens.Screen
import net.minecraft.util.Mth
import org.joml.component1
import org.joml.component2
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.*
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.extentions.translated
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.map.IslandData
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.dev.MapEditor
import tech.thatgravyboat.skycubed.features.map.dev.MapPoiEditScreen
import tech.thatgravyboat.skycubed.features.map.pois.Poi
import tech.thatgravyboat.skycubed.utils.Rect
import tech.thatgravyboat.skycubed.utils.getValue
import tech.thatgravyboat.skycubed.utils.setValue

class MapsWidget(
    map: String?,
    xOffset: State<Double> = State.of(McPlayer.self!!.position().x + Maps.getCurrentOffset().x),
    zOffset: State<Double> = State.of(McPlayer.self!!.position().z + Maps.getCurrentOffset().z),
    scale: State<Float> = State.of(1f),

    private val filter: (Poi) -> Boolean = Poi::enabled,
    width: Int,
    height: Int,

    val rotate: State<Boolean> = State.of(false),
    val shape: MapShape = MapShape.SQUARE,
) : BaseWidget(width, height) {

    private var xOffset by xOffset
    private var zOffset by zOffset
    private var scale by scale

    private val maps = Maps.getMaps(map)
    private val showPlayer = Maps.getMapsForLocation() == map

    private var cursor: Cursor = Cursor.DEFAULT

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.cursor = Cursor.DEFAULT

        val (posX, posY) = graphics.getTranslation()
        val (scaleX, scaleY) = graphics.getScale()

        graphics.scissor(x, y, width, height) {
            graphics.pushPop {
                graphics.translate(x.toFloat(), y.toFloat())
                graphics.scale(scale, scale)
                graphics.translate(-xOffset.toFloat(), -zOffset.toFloat())

                val headRot = Mth.rotLerp(partialTick, McPlayer.self!!.yHeadRotO, McPlayer.self!!.yHeadRot)
                // TODO: fix, in 1.21.8 the code for this seems to be broken if rotating the texture, needs to be fixed.
                if (rotate.get() && (!SkyCubed.is1218 || shape == MapShape.SQUARE)) {
                    graphics.rotate(180 - headRot, xOffset + width / 2, zOffset + height / 2)
                }

                maps.forEach { map ->
                    graphics.translated(map.topX + width / 2.0 + map.offsetX, map.topY + height / 2.0 + map.offsetY) {
                        val default = map.getDefaultTexture()
                        val texture = map.getTexture()

                        if (default != texture) {
                            shape.drawMapPart(graphics, default.getId(), map, posX, posY, width, height, scaleX, scaleY, 0xFF3F3F3F.toInt())
                        }

                        shape.drawMapPart(graphics, texture.getId(), map, posX, posY, width, height, scaleX, scaleY)
                    }

                    map.pois.forEach { poi ->
                        if (!filter(poi)) return@forEach

                        graphics.pushPop {
                            graphics.translate(poi.position.x + map.offsetX + width / 2f, poi.position.z + map.offsetY + height / 2f)
                            graphics.translate(-poi.bounds.x / 2f, -poi.bounds.y / 2f)
                            poi.display.render(graphics)

                            if (isMouseOver(map, poi.rect, mouseX - x, mouseY - y)) {
                                graphics.showTooltip(Text.multiline(poi.tooltip))
                                cursor = Cursor.POINTER
                            }
                        }
                    }

                    if (map.island == LocationAPI.island) {
                        MapWaypointsScreen.waypoints.forEach { waypoint ->
                            val position = waypoint.getPosition()
                            val mapX = position.x - 3 + map.offsetX + width / 2f
                            val mapY = position.z - 3 + map.offsetY + height / 2f

                            graphics.translated(mapX - 3f, mapY - 3f) {
                                graphics.drawSprite(SkyCubed.id("map/icons/waypoint"), 0, 0, 6, 6, waypoint.color)
                            }

                            if (isMouseOver(map, waypoint.toMapRect(), mouseX - x, mouseY - y)) {
                                graphics.showTooltip(
                                    Text.multiline(
                                        waypoint.name,
                                        Text.translatable("skycubed.map.waypoints.tooltip.subtitle"),
                                        CommonText.EMPTY,
                                        Text.translatable("skycubed.map.waypoints.tooltip.position"),
                                        Text.of(" ${position.x}, ${position.y}, ${position.z}"),
                                    ),
                                )
                                cursor = Cursor.POINTER
                            }
                        }
                    }
                }

                if (showPlayer) {
                    val offset = Maps.getCurrentPlayerOffset()
                    val x = McPlayer.self!!.x + offset.x
                    val z = McPlayer.self!!.z + offset.z
                    graphics.translated(x + width / 2.0f, z + height / 2.0f) {
                        val profile = McPlayer.skin ?: return
                        graphics.scale(1f / scale, 1f / scale)
                        graphics.rotate((180 + headRot).toDouble())
                        PlayerFaceRenderer.draw(graphics, profile, -4, -4, 8)
                    }
                }
            }
        }
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            xOffset -= dragX / scale
            zOffset -= dragY / scale
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        val oScale = scale
        scale += (scrollY / 5).toFloat()
        scale = scale.coerceAtLeast(0.5f).coerceAtMost(5f)

        xOffset -= mouseX / scale - mouseX / oScale
        zOffset -= mouseY / scale - mouseY / oScale
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val poi = getPoiAt(mouseX, mouseY)
        if (button == InputConstants.MOUSE_BUTTON_LEFT && poi != null) {
            if (MapEditor.enabled && !Screen.hasShiftDown()) {
                McClient.setScreenAsync { MapPoiEditScreen(poi.first, poi.second.pois, McClient.self.screen) }
                return true
            }
            poi.first.click()
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun getCursor() = this.cursor

    private fun isMouseOver(map: IslandData, rect: Rect, mouseX: Int, mouseY: Int): Boolean {
        if (!isMouseOver(mouseX.toDouble(), mouseY.toDouble())) return false

        val locX = (-xOffset + rect.x + map.offsetX + this.width / 2f + rect.width / 2) * scale
        val locZ = (-zOffset + rect.y + map.offsetY + this.height / 2f + rect.height / 2) * scale

        return locX in mouseX.toFloat()..mouseX + rect.width * scale && locZ in mouseY.toFloat()..mouseY + rect.height * scale
    }

    fun getWaypointAt(x: Number, y: Number): MeowddingWaypoint? = Maps.currentIsland?.let {
        MapWaypointsScreen.waypoints.find { waypoint ->
            isMouseOver(it, waypoint.toMapRect(), x.toInt() - this.x, y.toInt() - this.y)
        }
    }

    fun getPoiAt(x: Number, y: Number): Pair<Poi, IslandData>? {
        maps.forEach { map ->
            map.pois.forEach { poi ->
                if (isMouseOver(map, poi.rect, x.toInt() - this.x, y.toInt() - this.y) && filter(poi)) {
                    return poi to map
                }
            }
        }
        return null
    }

    fun removePoi(poi: Poi) = maps.forEach { map -> map.pois.remove(poi) }

    fun getWorldPosition(mouseX: Double, mouseY: Double): Pair<Double, Double> {
        val x = (mouseX - this.x) / scale + xOffset - this.width / 2.0
        val z = (mouseY - this.y) / scale + zOffset - this.height / 2.0
        return x to z
    }
}

fun MeowddingWaypoint.toMapRect(): Rect = Rect(getPosition().x.toInt() - 3, getPosition().z.toInt() - 3, 6, 6)
