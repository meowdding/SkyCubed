package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.icons.LocationIcons

object LocationInfo : InfoProvider {
    override val location = InfoLocation.TOP_LEFT

    override fun getDisplay() = DisplayFactory.horizontal {
        display(Displays.padding(1, LocationIcons))
        string(LocationAPI.area.name)
    }
}
