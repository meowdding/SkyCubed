package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay
import tech.thatgravyboat.skycubed.features.info.icons.LocationIcons

@RegisterInfoOverlay
object LocationInfo : InfoProvider {
    override val location = InfoLocation.TOP_LEFT

    override fun getDisplay() = DisplayFactory.horizontal {
        display(LocationIcons)
        string(LocationAPI.area.name)
    }
}
