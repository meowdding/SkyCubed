package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType
import tech.thatgravyboat.skyblockapi.helpers.McClient

@ConfigObject
class Position(
    @ConfigEntry(id = "x", type = EntryType.INTEGER) var x: Int = 0,
    @ConfigEntry(id = "y", type = EntryType.INTEGER) var y: Int = 0,
    @ConfigEntry(id = "scale", type = EntryType.FLOAT) var scale: Float = 1.0f
) {

    private val initialPos = x to y

    operator fun component1(): Int = if (x < 0) McClient.window.guiScaledWidth + x else x
    operator fun component2(): Int = if (y < 0) McClient.window.guiScaledHeight + y else y

    fun reset() {
        x = initialPos.first
        y = initialPos.second
    }
}