package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.math.Axis
import com.teamresourceful.resourcefullib.client.screens.CursorScreen.Cursor
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils
import earth.terrarium.olympus.client.components.base.BaseWidget
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.PlayerFaceRenderer
import net.minecraft.client.renderer.RenderType
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.pois.Poi
import tech.thatgravyboat.skycubed.utils.getValue
import tech.thatgravyboat.skycubed.utils.pushPop
import tech.thatgravyboat.skycubed.utils.scissor
import tech.thatgravyboat.skycubed.utils.setValue

class MapsWidget(
    map: String?,
    xOffset: State<Int> = State.of(McPlayer.self!!.blockPosition().x + Maps.getCurrentOffset().x),
    zOffset: State<Int> = State.of(McPlayer.self!!.blockPosition().z + Maps.getCurrentOffset().z),
    scale: State<Float> = State.of(1f),

    private val filter: (Poi) -> Boolean = Poi::enabled,
    width: Int,
    height: Int,
) : BaseWidget(width, height) {

    private var xOffset: Int by xOffset
    private var zOffset by zOffset
    private var scale by scale

    private val maps = Maps.getMaps(map)
    private val showPlayer = Maps.getMapsForLocation() == map

    private var cursor: Cursor = Cursor.DEFAULT

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.cursor = Cursor.DEFAULT

        graphics.scissor(x, y, width, height) {
            graphics.pushPop {
                translate(x.toFloat(), y.toFloat(), 0f)
                scale(scale, scale, 1f)
                translate(-xOffset.toFloat(), -zOffset.toFloat(), 0f)

                maps.forEach { map ->
                    graphics.pushPop {
                        val mapX = map.topX + width / 2f + map.offsetX
                        val mapY = map.topY + height / 2f + map.offsetY
                        translate(mapX, mapY, 0f)

                        val default = map.getDefaultTexture()
                        val texture = map.getTexture()
                        if (default != texture) {
                            graphics.blit(
                                RenderType::guiTextured,
                                default.getId(),
                                0, 0, 0f, 0f,
                                map.width, map.height, map.width, map.height,
                                0xFF3F3F3F.toInt()
                            )
                        }
                        graphics.blit(RenderType::guiTextured, texture.getId(), 0, 0, 0f, 0f, map.width, map.height, map.width, map.height)
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
                        val x = McPlayer.self!!.blockX + offset.x
                        val z = McPlayer.self!!.blockZ + offset.z
                        translate(x + width / 2f, z + height / 2f, 0f)
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
            xOffset -= (dragX.toInt() / scale).toInt()
            zOffset -= (dragY.toInt() / scale).toInt()
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        val oScale = scale
        scale += scrollY.toFloat() / 5
        scale = scale.coerceAtLeast(0.5f).coerceAtMost(5f)

        xOffset -= (mouseX / scale - mouseX / oScale).toInt()
        zOffset -= (mouseY / scale - mouseY / oScale).toInt()
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