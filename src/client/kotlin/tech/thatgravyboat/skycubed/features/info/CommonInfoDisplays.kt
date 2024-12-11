package tech.thatgravyboat.skycubed.features.info

import tech.thatgravyboat.skyblockapi.api.area.SlayerAPI
import tech.thatgravyboat.skyblockapi.api.area.SlayerType
import tech.thatgravyboat.skyblockapi.api.datetime.DateTimeAPI
import tech.thatgravyboat.skyblockapi.api.datetime.SkyBlockSeason
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.CurrencyAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.extentions.toRomanNumeral
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.features.info.icons.LocationIcons
import tech.thatgravyboat.skycubed.utils.capitalize
import tech.thatgravyboat.skycubed.utils.toOrdinal

object CommonInfoDisplays {

    val BASE = SkyCubed.id("info/base")
    val RIGHT_LINE = SkyCubed.id("info/right")
    val LEFT_LINE = SkyCubed.id("info/left")

    val locationDisplay = Displays.background(
        LEFT_LINE,
        Displays.padding(3, 1, 2, 2, Displays.row(
            Displays.padding(1, LocationIcons),
            Displays.text({ LocationAPI.area.name }),
        ))
    )

    private val riftSlayerIcon = Displays.sprite(SkyCubed.id("info/icons/slayers/vampire"), 8, 8)
    private val slayerIcon = Displays.supplied { when (SlayerAPI.type) {
        SlayerType.RIFTSTALKER_BLOODFIEND -> riftSlayerIcon
        else -> Displays.empty(8, 8)
    } }

    val slayerDisplay = Displays.background(
        LEFT_LINE,
        Displays.padding(
            3, 1, 2, 2, Displays.row(
                Displays.padding(1, slayerIcon),
                Displays.empty(2, 10),
                Displays.text(
                    {
                        val suffix = when {
                            SlayerAPI.max == 0 || SlayerAPI.current == 0 -> "§cInactive!"
                            SlayerAPI.current == SlayerAPI.max -> "§aComplete!"
                            SlayerAPI.text != null -> SlayerAPI.text
                            else ->  "§e${SlayerAPI.current}§7/§c${SlayerAPI.max}"
                        }
                        return@text "§a${SlayerAPI.level.toRomanNumeral()} $suffix"
                    }
                ),
            )
        )
    )

    val currencyDisplay = Displays.background(
        RIGHT_LINE,
        Displays.padding(0, 3, 2, 2, Displays.row(
            Displays.row(
                Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/purse"), 8, 8)),
                Displays.text(
                    { CurrencyAPI.purse.toFormattedString() },
                    { 0xFFAA00u }
                ),
            ),
            Displays.row(
                Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/bits"), 8, 8)),
                Displays.text(
                    { CurrencyAPI.bits.toFormattedString() },
                    { 0x55FFFFu }
                ),
            ),
        ))
    )

    private val springIcon = Displays.sprite(SkyCubed.id("info/icons/seasons/spring"), 8, 8)
    private val summerIcon = Displays.sprite(SkyCubed.id("info/icons/seasons/summer"), 8, 8)
    private val autumnIcon = Displays.sprite(SkyCubed.id("info/icons/seasons/autumn"), 8, 8)
    private val winterIcon = Displays.sprite(SkyCubed.id("info/icons/seasons/winter"), 8, 8)
    private val seasonIcon = Displays.supplied {
        when (DateTimeAPI.season) {
            SkyBlockSeason.EARLY_SPRING, SkyBlockSeason.SPRING, SkyBlockSeason.LATE_SPRING -> springIcon
            SkyBlockSeason.EARLY_SUMMER, SkyBlockSeason.SUMMER, SkyBlockSeason.LATE_SUMMER -> summerIcon
            SkyBlockSeason.EARLY_AUTUMN, SkyBlockSeason.AUTUMN, SkyBlockSeason.LATE_AUTUMN -> autumnIcon
            SkyBlockSeason.EARLY_WINTER, SkyBlockSeason.WINTER, SkyBlockSeason.LATE_WINTER -> winterIcon
            else -> Displays.empty(8, 8)
        }
    }

    val dateDisplay = Displays.background(
        RIGHT_LINE,
        Displays.padding(0, 3, 2, 2, Displays.row(
            Displays.padding(1, seasonIcon),
            Displays.text({ "${DateTimeAPI.season?.name?.capitalize().orEmpty()} ${DateTimeAPI.day.toOrdinal()}" }),
        ))
    )

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