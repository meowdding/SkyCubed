package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfigkt.api.ObjectKt
import tech.thatgravyboat.skyblockapi.helpers.McClient

class Position(
    x: Int = 0,
    y: Int = 0,
    scale: Float = 1.0f
) : ObjectKt() {

    var x: Int by int("x", x)
    var y: Int by int("y", y)
    var scale: Float by float("scale", scale)

    private val initialPos = x to y

    operator fun component1(): Int = if (x < 0) McClient.window.guiScaledWidth + x else x
    operator fun component2(): Int = if (y < 0) McClient.window.guiScaledHeight + y else y

    fun isRight(): Boolean = x < 0
    fun isBottom(): Boolean = y < 0

    fun reset() {
        x = initialPos.first
        y = initialPos.second
    }
}