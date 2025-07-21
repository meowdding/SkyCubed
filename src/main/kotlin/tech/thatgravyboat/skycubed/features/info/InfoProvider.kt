package tech.thatgravyboat.skycubed.features.info

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.CommonInfoDisplays.LEFT_LINE
import tech.thatgravyboat.skycubed.features.info.CommonInfoDisplays.RIGHT_LINE

interface InfoProvider {

    val islands: List<SkyBlockIsland> get() = emptyList()
    val areas: List<SkyBlockArea> get() = emptyList()

    val location: InfoLocation

    fun getDisplay(): Display

    fun shouldDisplay() = (islands.isEmpty() || SkyBlockIsland.inAnyIsland(islands)) && (areas.isEmpty() || SkyBlockArea.inAnyArea(*areas.toTypedArray()))

    fun getIconDisplay(string: String) = getIconDisplay(SkyCubed.id(string))
    fun getIconDisplay(location: ResourceLocation) = Displays.padding(1, Displays.sprite(location, 8, 8))
}

enum class InfoLocation(val withBackground: (Display) -> Display) {
    TOP_LEFT({ Displays.background(LEFT_LINE, Displays.padding(3, 1, 2, 2, it)) }),
    TOP_RIGHT({ Displays.background(RIGHT_LINE, Displays.padding(0, 3, 2, 2, it)) }),
    BOTTOM_LEFT({ Displays.background(LEFT_LINE, Displays.padding(3, 1, 2, 2, it)) }),
    BOTTOM_RIGHT({ Displays.background(RIGHT_LINE, Displays.padding(0, 3, 2, 2, it)) }),
}
