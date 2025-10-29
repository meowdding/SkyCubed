package tech.thatgravyboat.skycubed.features.info

import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.area.rift.RiftAPI
import tech.thatgravyboat.skyblockapi.api.datetime.DateTimeAPI
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skycubed.SkyCubed
import kotlin.time.Duration

// TODO: island specific base element overrides
object BaseInfoDisplay {

    val BASE = SkyCubed.id("info/base")
    val RIGHT_LINE = SkyCubed.id("info/right")
    val LEFT_LINE = SkyCubed.id("info/left")

    private val sunIcon = icon("sun")
    private val moonIcon = icon("moon")

    private val clockIcon = icon("rift/clock")
    private val pausedIcon = icon("rift/paused")

    private val dungeonFloorIcon = listOf(
        icon("dungeons/entrance"),
        icon("dungeons/bonzo"),
        icon("dungeons/scarf"),
        icon("dungeons/professor"),
        icon("dungeons/thorn"),
        icon("dungeons/livid"),
        icon("dungeons/sadan"),
        icon("dungeons/wither"),
    )

    val baseDisplay = DisplayFactory.vertical {
        spacer(34, 5)
        display(Displays.center(34, 12, Displays.supplied { getIcon() }))
        spacer(34, 3)
        display(Displays.center(34, 10, Displays.supplied { Displays.text(getText(), ::getTextColor) }))
        spacer(34, 1)
    }

    private fun icon(id: String) = Displays.sprite(SkyCubed.id("info/icons/$id"), 8, 8)

    private fun getIcon() = when (LocationAPI.island) {
        SkyBlockIsland.THE_RIFT -> if (isTimePaused()) pausedIcon else clockIcon
        SkyBlockIsland.THE_CATACOMBS -> DungeonAPI.dungeonFloor?.floorNumber?.let { dungeonFloorIcon[it] } ?: clockIcon
        else -> if (DateTimeAPI.isDay) sunIcon else moonIcon
    }

    private fun getText() = when (LocationAPI.island) {
        SkyBlockIsland.THE_RIFT -> getRiftTime()
        SkyBlockIsland.THE_CATACOMBS -> toBeautiful(DungeonAPI.time)
        else -> toBeautiful(DateTimeAPI.hour, DateTimeAPI.minute)
    }

    private fun getTextColor() = when (LocationAPI.island) {
        SkyBlockIsland.THE_RIFT -> if (isTimePaused()) 0xAAAAAAu else 0x55FF55u
        SkyBlockIsland.THE_CATACOMBS -> 0x55FF55u
        else -> if (DateTimeAPI.isDay) 0xFFFF55u else 0xAAAAAAu
    }


    private val pausedRiftTimeAreas = setOf(
        SkyBlockAreas.WIZARD_TOWER,
        SkyBlockAreas.RIFT_GALLERY,
        SkyBlockAreas.RIFT_GALLERY_ENTRANCE,
        SkyBlockAreas.MIRRORVERSE,
    )

    private fun getRiftTime(): String = RiftAPI.time?.let { toBeautiful(it) } ?: "0s"

    private fun isTimePaused(): Boolean = LocationAPI.area in pausedRiftTimeAreas

    private fun toBeautiful(duration: Duration) = toBeautiful(duration.inWholeMinutes, duration.inWholeSeconds % 60)
    private fun toBeautiful(first: Number, second: Number) = buildString {
        append(first.toString().padStart(2, '0'))
        append(":")
        append(second.toString().padStart(2, '0'))
    }
}
