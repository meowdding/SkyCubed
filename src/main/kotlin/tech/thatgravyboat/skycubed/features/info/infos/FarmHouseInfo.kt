package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.area.hub.FarmhouseAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@RegisterInfoOverlay
object FarmHouseInfo : InfoProvider {
    override val location = InfoLocation.BOTTOM_LEFT

    override val areas = listOf(SkyBlockAreas.FARMHOUSE, SkyBlockAreas.GARDEN)
    override val islands = listOf(SkyBlockIsland.HUB, SkyBlockIsland.GARDEN)

    override fun getDisplay() = DisplayFactory.horizontal {
        display(getIconDisplay(SkyCubed.id("info/icons/gold")))
        textDisplay(FarmhouseAPI.goldMedals.toFormattedString()) { color = TextColor.GOLD }

        display(getIconDisplay(SkyCubed.id("info/icons/silver")))
        textDisplay(FarmhouseAPI.silverMedals.toFormattedString()) { color = TextColor.WHITE }

        display(getIconDisplay(SkyCubed.id("info/icons/bronze")))
        textDisplay(FarmhouseAPI.bronzeMedals.toFormattedString()) { color = TextColor.RED }
    }
}
