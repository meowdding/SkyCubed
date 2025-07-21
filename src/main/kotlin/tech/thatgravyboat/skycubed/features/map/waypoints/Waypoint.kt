package tech.thatgravyboat.skycubed.features.map.waypoints

import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor
import org.joml.Vector3f
import tech.thatgravyboat.skycubed.utils.Rect

data class Waypoint(
    val text: Component,
    val pos: Vector3f,
    val color: Int = DyeColor.PURPLE.textureDiffuseColor,
    val ignoreY: Boolean = false,
) {

    fun toMapRect(): Rect = Rect(pos.x.toInt() - 3, pos.z.toInt() - 3, 6, 6)
}
