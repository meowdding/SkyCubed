package tech.thatgravyboat.skycubed.features.info

import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.area.rift.RiftAPI
import tech.thatgravyboat.skyblockapi.api.datetime.DateTimeAPI
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skycubed.SkyCubed

object BaseInfoDisplay {

    val BASE = SkyCubed.id("info/base")
    val RIGHT_LINE = SkyCubed.id("info/right")
    val LEFT_LINE = SkyCubed.id("info/left")

    private val sunIcon = Displays.sprite(SkyCubed.id("info/icons/sun"), 8, 8)
    private val moonIcon = Displays.sprite(SkyCubed.id("info/icons/moon"), 8, 8)

    private val clockIcon = Displays.sprite(SkyCubed.id("info/icons/rift/clock"), 8, 8)
    private val pausedIcon = Displays.sprite(SkyCubed.id("info/icons/rift/paused"), 8, 8)

    val baseDisplay = DisplayFactory.vertical {
        spacer(34, 5)
        display(Displays.center(34, 12, Displays.supplied { getIcon() }))
        spacer(34, 3)
        display(Displays.center(34, 10, Displays.supplied { Displays.text(getText(), ::getTextColor) }))
        spacer(34, 1)
    }

    private fun getIcon() = when (LocationAPI.island) {
        SkyBlockIsland.THE_RIFT -> if (isTimePaused()) pausedIcon else clockIcon
        else -> if (DateTimeAPI.isDay) sunIcon else moonIcon
    }

    private fun getText() = when (LocationAPI.island) {
        SkyBlockIsland.THE_RIFT -> getRiftTime()
        else -> "${DateTimeAPI.hour.toString().padStart(2, '0')}:${DateTimeAPI.minute.toString().padStart(2, '0')}"
    }

    private fun getTextColor() = when (LocationAPI.island) {
        SkyBlockIsland.THE_RIFT -> if (isTimePaused()) 0xAAAAAAu else 0x55FF55u
        else -> if (DateTimeAPI.isDay) 0xFFFF55u else 0xAAAAAAu
    }


    private val pausedRiftTimeAreas = setOf(
        SkyBlockAreas.WIZARD_TOWER,
        SkyBlockAreas.RIFT_GALLERY,
        SkyBlockAreas.RIFT_GALLERY_ENTRANCE,
        SkyBlockAreas.MIRRORVERSE,
    )

    private fun getRiftTime(): String = RiftAPI.time?.let {
        val minutes = "${it.inWholeMinutes}".padStart(2, '0')
        val seconds = "${it.inWholeSeconds % 60}".padStart(2, '0')
        "$minutes:$seconds"
    } ?: "0s"

    private fun isTimePaused(): Boolean = LocationAPI.area in pausedRiftTimeAreas

}
