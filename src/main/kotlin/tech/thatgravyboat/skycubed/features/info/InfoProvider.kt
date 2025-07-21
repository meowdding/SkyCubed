package tech.thatgravyboat.skycubed.features.info

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skycubed.SkyCubed

interface InfoProvider {

    val islands: List<SkyBlockIsland> get() = emptyList()
    val areas: List<SkyBlockArea> get() = emptyList()

    val location: InfoLocation

    fun getDisplay(): Display

    fun getIconDisplay(string: String) = getIconDisplay(SkyCubed.id(string))
    fun getIconDisplay(location: ResourceLocation) = Displays.padding(1, Displays.sprite(location, 8, 8))
}

enum class InfoLocation {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
}
