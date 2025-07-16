package tech.thatgravyboat.skycubed.features.map.waypoints

import me.owdding.ktmodules.Module
import net.minecraft.client.renderer.blockentity.BeaconRenderer
import net.minecraft.network.chat.Component
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.world.item.DyeColor
import org.joml.*
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.translated
import kotlin.math.atan2
import kotlin.math.roundToInt

@Module
object Waypoints {

    private val waypoints = mutableListOf<Waypoint>()

    fun addWaypoint(text: Component, x: Number, y: Number, z: Number, color: DyeColor = DyeColor.WHITE, ignoreY: Boolean = false) {
        waypoints.add(Waypoint(
            text = text,
            pos = Vector3f(x.toFloat(), y.toFloat(), z.toFloat()),
            color = color.textureDiffuseColor,
            ignoreY = ignoreY
        ))
    }

    fun removeWaypoint(waypoint: Waypoint) {
        waypoints.remove(waypoint)
    }

    fun waypoints(): List<Waypoint> {
        return waypoints
    }

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        waypoints.clear()
    }

    @Subscription
    fun onRenderWorld(event: RenderWorldEvent.AfterTranslucent) {
        val cameraPos = event.camera.position.toVector3f()
        val stack = event.poseStack
        val font = McFont.self

        event.atCamera {
            waypoints.removeIf { waypoint ->
                val text = waypoint.text
                val (x, y, z) = waypoint.pos
                val color = waypoint.color
                val distance = if (waypoint.ignoreY) {
                    Mth.sqrt((cameraPos.x - x) * (cameraPos.x - x) + (cameraPos.z - z) * (cameraPos.z - z))
                } else {
                    Mth.sqrt((cameraPos.x - x) * (cameraPos.x - x) + (cameraPos.y - y) * (cameraPos.y - y) + (cameraPos.z - z) * (cameraPos.z - z))
                }

                stack.translated(x, y, z) {
                    BeaconRenderer.renderBeaconBeam(
                        stack, event.buffer, BeaconRenderer.BEAM_LOCATION,
                        0f, Mth.PI, McLevel.self.gameTime, 0, McLevel.self.maxY * 2,
                        ARGB.opaque(color), 0.2f, 0.25f
                    )

                    if (distance > 5) {
                        var yOffset = 2f
                        if (waypoint.ignoreY) {
                            val playerY = McPlayer.position?.y ?: 0.0
                            yOffset += (playerY / 16).roundToInt() * 16f
                        }

                        translate(0.5f, yOffset, 0.5f)
                        mulPose(Quaternionf().rotateY(-Mth.DEG_TO_RAD * Mth.wrapDegrees((atan2(cameraPos.z - z, cameraPos.x - x) * Mth.RAD_TO_DEG) - 90.0f)))

                        val scale = (distance * 0.0025f).coerceAtMost(0.4f)
                        scale(0.05f + scale, -0.05f - scale, 0.05f + scale)

                        // TODO
//                         font.drawInBatch(
//                             text, -font.width(text) / 2f, -4f, -1, false,
//                             this.last().pose(), event.buffer,
//                             Font.DisplayMode.SEE_THROUGH,
//                             0,
//                             LightTexture.FULL_BRIGHT
//                         )

                        val distanceText = "${distance.toInt()}m"

//                         font.drawInBatch(
//                             distanceText, -font.width(distanceText) / 2f, 6f, -1, false,
//                             this.last().pose(), event.buffer,
//                             Font.DisplayMode.SEE_THROUGH,
//                             0,
//                             LightTexture.FULL_BRIGHT
//                         )
                    }
                }

                distance < 5f
            }
        }
    }
}
