package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.hotm.PowderAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@RegisterInfoOverlay
object PowderInfo : InfoProvider {
    private val glaciteAreas = listOf(
        SkyBlockAreas.GREAT_LAKE,
        SkyBlockAreas.GLACITE_TUNNELS,
        SkyBlockAreas.BASECAMP,
        SkyBlockAreas.FOSSIL_RESEARCH,
    )

    override val location = InfoLocation.BOTTOM_LEFT

    override val islands: List<SkyBlockIsland> = listOf(SkyBlockIsland.DWARVEN_MINES, SkyBlockIsland.CRYSTAL_HOLLOWS, SkyBlockIsland.MINESHAFT)

    override fun getDisplay() = DisplayFactory.horizontal {
        val (icon, powder) = when (LocationAPI.island) {
            SkyBlockIsland.DWARVEN_MINES -> {
                if (SkyBlockArea.Companion.inAnyArea(*glaciteAreas.toTypedArray())) {
                    SkyCubed.id("info/icons/glacite") to (PowderAPI.glacite to TextColor.AQUA)
                } else {
                    SkyCubed.id("info/icons/mithril") to (PowderAPI.mithril to 0x55FFFF)
                }
            }

            SkyBlockIsland.CRYSTAL_HOLLOWS -> SkyCubed.id("info/icons/gemstone") to (PowderAPI.gemstone to TextColor.LIGHT_PURPLE)
            SkyBlockIsland.MINESHAFT -> SkyCubed.id("info/icons/glacite") to (PowderAPI.glacite to TextColor.AQUA)
            else -> return@horizontal
        }
        display(getIconDisplay(icon))
        textDisplay(powder.first.toFormattedString(), shadow = true) { color = powder.second }
    }

}
