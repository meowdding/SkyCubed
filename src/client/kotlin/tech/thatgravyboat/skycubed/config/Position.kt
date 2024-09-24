package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType
import tech.thatgravyboat.skyblockapi.helpers.McClient

@ConfigObject
class Position(
    @field:ConfigEntry(id = "x", type = EntryType.INTEGER) var x: Int = 0,
    @field:ConfigEntry(id = "y", type = EntryType.INTEGER) var y: Int = 0,
) {

    private val initialPos = x to y

    operator fun component1(): Int = if (x < 0) McClient.window.guiScaledWidth + x else x
    operator fun component2(): Int = if (y < 0) McClient.window.guiScaledHeight + y else y

    fun reset() {
        x = initialPos.first
        y = initialPos.second
    }
}