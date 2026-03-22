package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.garden.PlotAPI
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@RegisterInfoOverlay
object PestInfo : InfoProvider {
    override val location: InfoLocation = InfoLocation.BOTTOM_LEFT

    override fun getDisplay() = DisplayFactory.horizontal {
        display(getIconDisplay(SkyCubed.id("info/icons/pest")))
        textDisplay("${PlotAPI.currentPestAmount}", shadow = true) { color = TextColor.DARK_GREEN }
    }

    override fun shouldDisplay() = SkyBlockIsland.GARDEN.inIsland() && PlotAPI.currentPestAmount > 0
}
