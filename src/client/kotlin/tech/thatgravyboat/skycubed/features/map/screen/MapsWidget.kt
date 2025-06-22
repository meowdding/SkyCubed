package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.math.Axis
import com.teamresourceful.resourcefullib.client.screens.CursorScreen.Cursor
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils
import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import net.minecraft.resources.ResourceLocation
import org.joml.Vector3f
import org.joml.component1
import org.joml.component2
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skyblockapi.utils.extentions.scissor
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.features.map.IslandData
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.pois.Poi
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

                if (rotate.get()) rotateAround(
                    Axis.ZP.rotationDegrees(180 - McPlayer.self!!.yHeadRot),
                    (xOffset + width / 2).toFloat(),
                    (zOffset + height / 2).toFloat(),
                    0.0f,
                )

                maps.forEach { map ->
                    graphics.pushPop {
                        val mapX = map.topX + width / 2.0 + map.offsetX
                        val mapY = map.topY + height / 2.0 + map.offsetY
                        translate(mapX, mapY, 0.0)

                        val default = map.getDefaultTexture()
                        val texture = map.getTexture()

                        if (default != texture) {
                            shape.drawMapPart(
                                graphics,
                                default.getId(),
                                map,
                                posX,
                                posY,
                                width,
                                height,
                                scaleX,
                                scaleY,
                                0xFF3F3F3F.toInt(),
                            )
                        }

                        shape.drawMapPart(
                            graphics,
                            texture.getId(),
                            map,
                            posX,
                            posY,
                            width,
                            height,
                            scaleX,
                            scaleY,
                        )
                    }

                    map.pois.forEachIndexed { index, poi ->
                        if (!filter(poi)) return@forEachIndexed

                        graphics.pushPop {
                            val mapX = poi.position.x + width / 2f
                            val mapY = poi.position.y + height / 2f
                            translate(mapX, mapY, 0f)
                            translate(-poi.bounds.x / 2f, -poi.bounds.y / 2f, 0f)
                            poi.display.render(graphics)

                            if (isMouseOver(poi, mouseX - x, mouseY - y)) {
                                if (McClient.isDev) {
                                    ScreenUtils.setTooltip(poi.tooltip + listOf(CommonText.EMPTY, Text.of("Id: $index")))
                                } else {
                                    ScreenUtils.setTooltip(poi.tooltip)
                                }
                                cursor = Cursor.POINTER
                            }
                        }
                    }
                }

                if (showPlayer) {
                    graphics.pushPop {
                        val offset = Maps.getCurrentPlayerOffset()
                        val x = McPlayer.self!!.x + offset.x
                        val z = McPlayer.self!!.z + offset.z
                        translate(x + width / 2.0, z + height / 2.0, 0.0)
                        val profile = McPlayer.skin ?: return
                        scale(1f / scale, 1f / scale, 1f)

                        rotateAround(Axis.ZP.rotationDegrees(180 + McPlayer.self!!.yHeadRot), 0f, 0f, 0f)

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
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            maps.forEach { map ->
                map.pois.forEach { poi ->
                    if (isMouseOver(poi, mouseX.toInt() - x, mouseY.toInt() - y) && filter(poi)) {
                        poi.click()
                        return true
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun getCursor() = this.cursor

    private fun isMouseOver(poi: Poi, mouseX: Int, mouseY: Int): Boolean {
        if (!isMouseOver(mouseX.toDouble(), mouseY.toDouble())) return false

        val locX = (-xOffset + poi.position.x + this.width / 2f + poi.bounds.x / 2) * scale
        val locZ = (-zOffset + poi.position.y + this.height / 2f + poi.bounds.y / 2) * scale

        return locX in mouseX.toFloat()..mouseX + poi.bounds.x * scale && locZ in mouseY.toFloat()..mouseY + poi.bounds.y * scale
    }
}

enum class MapShape(
    val displayName: String,
) {
    CIRCLE("Circle"),
    SQUARE("Square"),
    ;

    fun drawMapPart(
        graphics: GuiGraphics,
        texture: ResourceLocation,
        map: IslandData,
        posX: Float,
        posY: Float,
        width: Int,
        height: Int,
        scaleX: Float,
        scaleY: Float,
        color: Int = -1,
    ) = when (this) {
        SQUARE -> graphics.blit(
            net.minecraft.client.renderer.RenderType::guiTextured,
            texture,
            0, 0, 0f, 0f,
            map.width, map.height, map.width, map.height,
            color,
        )

        CIRCLE -> CircularMinimapRenderer.drawMapPart(
            graphics,
            texture,
            posX + width * scaleX / 2.0f + 1,
            posY + height * scaleY / 2.0f + 1,
            width * kotlin.math.min(scaleX, scaleY) / 2.0f,
            0, 0,
            0f, 0f,
            map.width, map.height,
            map.width, map.height,
            color,
        )
    }

    override fun toString() = displayName

    val next by lazy {
        entries[(ordinal + 1) % entries.size]
    }
}
