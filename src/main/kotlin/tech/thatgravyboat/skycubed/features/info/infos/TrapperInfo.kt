package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.area.farming.TrapperAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider

object TrapperInfo : InfoProvider {
    override val location = InfoLocation.BOTTOM_LEFT

    override val islands = listOf(SkyBlockIsland.THE_BARN)

    override fun getDisplay() = DisplayFactory.horizontal {
        display(getIconDisplay(SkyCubed.id("info/icons/pelts")))

        textDisplay(TrapperAPI.pelts.toFormattedString()) { color = TextColor.DARK_PURPLE }
    }
}
