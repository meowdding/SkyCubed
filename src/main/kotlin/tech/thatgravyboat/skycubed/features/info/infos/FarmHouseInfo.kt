package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import net.minecraft.world.phys.AABB
import tech.thatgravyboat.skyblockapi.api.area.hub.FarmhouseAPI
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McPlayer.contains
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@RegisterInfoOverlay
object FarmHouseInfo : InfoProvider {
    private val barnAABB = AABB(-48.0, 60.0, -48.0, 47.0, 120.0, 47.0)
    override val location = InfoLocation.BOTTOM_LEFT

    override fun shouldDisplay() = when (LocationAPI.island) {
        SkyBlockIsland.HUB -> SkyBlockAreas.FARMHOUSE.inArea()
        SkyBlockIsland.GARDEN -> McPlayer in barnAABB
        else -> false
    }

    override fun getDisplay() = DisplayFactory.horizontal {
        display(getIconDisplay(SkyCubed.id("info/icons/gold")))
        textDisplay(FarmhouseAPI.goldMedals.toFormattedString(), shadow = true) { color = TextColor.GOLD }

        display(getIconDisplay(SkyCubed.id("info/icons/silver")))
        textDisplay(FarmhouseAPI.silverMedals.toFormattedString(), shadow = true) { color = TextColor.WHITE }

        display(getIconDisplay(SkyCubed.id("info/icons/bronze")))
        textDisplay(FarmhouseAPI.bronzeMedals.toFormattedString(), shadow = true) { color = TextColor.RED }
    }
}
