package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.datetime.DateTimeAPI
import tech.thatgravyboat.skyblockapi.api.datetime.SkyBlockSeason
import tech.thatgravyboat.skyblockapi.utils.extentions.toTitleCase
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.utils.toOrdinal

object DateInfo : InfoProvider {
    private val springIcon = getIconDisplay("info/icons/seasons/spring")
    private val autumnIcon = getIconDisplay("info/icons/seasons/autumn")
    private val winterIcon = getIconDisplay("info/icons/seasons/winter")
    private val summerIcon = getIconDisplay("info/icons/seasons/summer")

    override val location = InfoLocation.TOP_RIGHT

    override fun getDisplay() = DisplayFactory.horizontal {
        val seasonIcon = when (DateTimeAPI.season) {
            SkyBlockSeason.EARLY_SPRING, SkyBlockSeason.SPRING, SkyBlockSeason.LATE_SPRING -> springIcon
            SkyBlockSeason.EARLY_SUMMER, SkyBlockSeason.SUMMER, SkyBlockSeason.LATE_SUMMER -> summerIcon
            SkyBlockSeason.EARLY_AUTUMN, SkyBlockSeason.AUTUMN, SkyBlockSeason.LATE_AUTUMN -> autumnIcon
            SkyBlockSeason.EARLY_WINTER, SkyBlockSeason.WINTER, SkyBlockSeason.LATE_WINTER -> winterIcon
            else -> Displays.empty(8, 8)
        }
        display(Displays.padding(1, seasonIcon))
        string("${DateTimeAPI.season?.name?.toTitleCase().orEmpty()} ${DateTimeAPI.day.toOrdinal()}")
    }
}
