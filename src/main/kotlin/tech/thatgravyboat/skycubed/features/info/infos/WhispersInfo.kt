package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.hotf.WhispersAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay


@RegisterInfoOverlay
object WhispersInfo : InfoProvider {
    override val location = InfoLocation.BOTTOM_LEFT

    override val islands: List<SkyBlockIsland> = listOf(SkyBlockIsland.GALATEA)

    override fun getDisplay() = DisplayFactory.horizontal {
        display(getIconDisplay(SkyCubed.id("info/icons/forest")))
        textDisplay(WhispersAPI.forest.toFormattedString(), shadow = true) { color = TextColor.DARK_AQUA }
    }
}
