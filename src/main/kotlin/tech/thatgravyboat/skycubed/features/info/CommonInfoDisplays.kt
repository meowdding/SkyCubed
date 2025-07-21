package tech.thatgravyboat.skycubed.features.info

import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.datetime.DateTimeAPI
import tech.thatgravyboat.skycubed.SkyCubed

object CommonInfoDisplays {

    val BASE = SkyCubed.id("info/base")
    val RIGHT_LINE = SkyCubed.id("info/right")
    val LEFT_LINE = SkyCubed.id("info/left")

    private val sunIcon = Displays.sprite(SkyCubed.id("info/icons/sun"), 8, 8)
    private val moonIcon = Displays.sprite(SkyCubed.id("info/icons/moon"), 8, 8)
    val baseDisplay = Displays.column(
        Displays.empty(34, 5),
        Displays.center(
            34, 12,
            Displays.supplied { if (DateTimeAPI.isDay) sunIcon else moonIcon }
        ),
        Displays.empty(34, 3),
        Displays.center(
            34, 10,
            Displays.text(
                { "${DateTimeAPI.hour.toString().padStart(2, '0')}:${DateTimeAPI.minute.toString().padStart(2, '0')}" },
                { if (DateTimeAPI.isDay) 0xFFFF55u else 0xAAAAAAu }
            )
        ),
        Displays.empty(34, 1),
    )

}
