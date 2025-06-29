package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.math.Axis
import com.teamresourceful.resourcefullib.client.screens.CursorScreen.Cursor
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils
import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.util.Mth
import org.joml.Vector3f
import org.joml.component1
import org.joml.component2
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
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
import tech.thatgravyboat.skycubed.features.map.waypoints.Waypoint
import tech.thatgravyboat.skycubed.features.map.waypoints.Waypoints
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

        val (posX, posY) = graphics.pose().last().pose().getTranslation(Vector3f())
        val (scaleX, scaleY) = graphics.pose().last().pose().getScale(Vector3f())

        graphics.scissor(x, y, width, height) {
            graphics.pushPop {
                translate(x.toFloat(), y.toFloat(), 0f)
                scale(scale, scale, 1f)
                translate(-xOffset.toFloat(), -zOffset.toFloat(), 0.0f)

                val headRot = Mth.rotLerp(partialTick, McPlayer.self!!.yHeadRotO, McPlayer.self!!.yHeadRot)
                if (rotate.get()) {
                    rotateAround(
                        Axis.ZP.rotationDegrees(180 - headRot),
                        (xOffset + width / 2).toFloat(),
                        (zOffset + height / 2).toFloat(),
                        0.0f,
                    )
                }

                maps.forEach { map ->
                    graphics.translated(map.topX + width / 2.0 + map.offsetX, map.topY + height / 2.0 + map.offsetY, 0f) {
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
                            translate(poi.position.x + map.offsetX + width / 2f, poi.position.z + map.offsetY + height / 2f, 0f)
                            translate(-poi.bounds.x / 2f, -poi.bounds.y / 2f, 0f)
                            poi.display.render(graphics)

                            if (isMouseOver(map, poi.rect, mouseX - x, mouseY - y)) {
                                ScreenUtils.setTooltip(poi.tooltip)
                                cursor = Cursor.POINTER
                            }
                        }
                    }

                    if (map.island == LocationAPI.island) {
                        Waypoints.waypoints().forEach { waypoint ->
                            val mapX = waypoint.pos.x - 3 + map.offsetX + width / 2f
                            val mapY = waypoint.pos.z - 3 + map.offsetY + height / 2f

                            graphics.translated(mapX - 3f, mapY - 3f, 0f) {
                                graphics.blitSprite(RenderType::guiTextured, SkyCubed.id("map/icons/waypoint"), 0, 0, 6, 6, waypoint.color)
                            }

                            if (isMouseOver(map, waypoint.toMapRect(), mouseX - x, mouseY - y)) {
                                ScreenUtils.setTooltip(
                                    listOf(
                                        waypoint.text,
                                        Text.translatable("skycubed.map.waypoints.tooltip.subtitle"),
                                        CommonText.EMPTY,
                                        Text.translatable("skycubed.map.waypoints.tooltip.position"),
                                        Text.of(" ${waypoint.pos.x}, ${waypoint.pos.y}, ${waypoint.pos.z}"),
                                    )
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
                    graphics.translated(x + width / 2.0f, z + height / 2.0f, 0f) {
                        val profile = McPlayer.skin ?: return
                        scale(1f / scale, 1f / scale, 1f)

                        rotateAround(Axis.ZP.rotationDegrees(180 + headRot), 0f, 0f, 0f)

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

    fun getWaypointAt(x: Number, y: Number): Waypoint? = Maps.currentIsland?.let { Waypoints.waypoints().find { waypoint ->
        isMouseOver(it, waypoint.toMapRect(), x.toInt() - this.x, y.toInt() - this.y)
    } }

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
