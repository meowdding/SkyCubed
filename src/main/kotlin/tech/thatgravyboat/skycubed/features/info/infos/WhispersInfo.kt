package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.hotf.WhispersAPI
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@RegisterInfoOverlay
object WhispersInfo : InfoProvider {
    override val location = InfoLocation.BOTTOM_LEFT

    override val islands: List<SkyBlockIsland> = listOf(SkyBlockIsland.GALATEA, SkyBlockIsland.TORRHUS_CANYON)

    override fun getDisplay() = DisplayFactory.horizontal {
        val (icon, whispers) = when (LocationAPI.island) {
            SkyBlockIsland.GALATEA -> SkyCubed.id("info/icons/forest") to (WhispersAPI.forest to TextColor.DARK_AQUA)
            SkyBlockIsland.TORRHUS_CANYON -> SkyCubed.id("info/icons/desert") to (WhispersAPI.desert to TextColor.GOLD)
            else -> return@horizontal
        }
        display(getIconDisplay(icon))
        textDisplay(whispers.first.format(), shadow = true) { color = whispers.second }
    }
}
