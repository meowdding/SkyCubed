package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption
import tech.thatgravyboat.skycubed.config.overlays.Position

@Category("pickUpLog")
object PickUpLogConfig {
    @ConfigEntry(id = "pickUpLogEnabled")
    var enabled = true

    // todo better pos
    @ConfigOption.Hidden
    @ConfigEntry(id = "pickUpLogPosition")
    val position = Position(5, 5)
}