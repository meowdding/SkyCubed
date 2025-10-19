package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.area.rift.RiftAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@RegisterInfoOverlay
object RiftInfo : InfoProvider {
    override val islands = listOf(SkyBlockIsland.THE_RIFT)
    override val priority: Int = 2
    override val location = InfoLocation.TOP_RIGHT

    override fun getDisplay() = DisplayFactory.horizontal {
        display(getIconDisplay(SkyCubed.id("info/icons/rift/timecharm")))
        textDisplay("${RiftAPI.timecharms.first}/${RiftAPI.timecharms.second}", shadow = true) { color = 0xFF5555 }

        display(getIconDisplay(SkyCubed.id("info/icons/rift/enigma")))
        textDisplay("${RiftAPI.enigmaSouls.first}/${RiftAPI.enigmaSouls.second}", shadow = true) { color = 0xAA00AA }
    }

}
