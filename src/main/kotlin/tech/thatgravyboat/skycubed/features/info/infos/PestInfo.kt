package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.garden.PlotAPI
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@RegisterInfoOverlay
object PestInfo : InfoProvider {
    override val location: InfoLocation = InfoLocation.BOTTOM_LEFT

    override fun getDisplay(): Display = Displays.text("${PlotAPI.currentPestAmount} ൠ", color = { TextColor.DARK_GREEN.toUInt() })

    override fun shouldDisplay() = SkyBlockIsland.GARDEN.inIsland() && PlotAPI.currentPestAmount > 0

}
