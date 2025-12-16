package tech.thatgravyboat.skycubed.features.info

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.withPadding
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.BaseInfoDisplay.LEFT_LINE
import tech.thatgravyboat.skycubed.features.info.BaseInfoDisplay.RIGHT_LINE

interface InfoProvider {

    val islands: List<SkyBlockIsland> get() = emptyList()
    val areas: List<SkyBlockArea> get() = emptyList()
    val priority: Int get() = 0

    val location: InfoLocation

    fun getDisplay(): Display

    fun shouldDisplay() = (islands.isEmpty() || SkyBlockIsland.inAnyIsland(islands)) && (areas.isEmpty() || SkyBlockArea.inAnyArea(*areas.toTypedArray()))

    fun getIconDisplay(string: String) = getIconDisplay(SkyCubed.id(string))
    fun getIconDisplay(location: Identifier) = Displays.sprite(location, 8, 8).withPadding(left = 1, right = 1)

    companion object {
        operator fun invoke(
            areas: List<SkyBlockArea> = emptyList(),
            islands: List<SkyBlockIsland> = emptyList(),
            predicate: (() -> Boolean)? = null,
            displayProvider: InfoProvider.() -> Display
        ) = of(areas, islands, predicate, displayProvider)
        fun of(
            areas: List<SkyBlockArea> = emptyList(),
            islands: List<SkyBlockIsland> = emptyList(),
            predicate: (() -> Boolean)? = null,
            displayProvider: InfoProvider.() -> Display
        ): InfoProvider = DefaultInfoProvider(areas, islands, predicate, displayProvider)
    }
}

private class DefaultInfoProvider(
    override val areas: List<SkyBlockArea> = emptyList(),
    override val islands: List<SkyBlockIsland> = emptyList(),
    val predicate: (() -> Boolean)? = null,
    val displayProvider: InfoProvider.() -> Display
) : InfoProvider {
    override val location: InfoLocation get() = throw UnsupportedOperationException("Default providers should only be used in overrides!")
    override fun getDisplay() = displayProvider()
    override fun shouldDisplay(): Boolean = if (predicate == null) super.shouldDisplay() else predicate()
}

enum class InfoLocation(val withBackground: (Display) -> Display) {
    TOP_LEFT({ Displays.background(LEFT_LINE, Displays.padding(3, 1, 3, 2, it)) }),
    TOP_RIGHT({ Displays.background(RIGHT_LINE, Displays.padding(0, 3, 3, 2, it)) }),
    BOTTOM_LEFT({ Displays.background(LEFT_LINE, Displays.padding(3, 1, 3, 2, it)) }),
    BOTTOM_RIGHT({ Displays.background(RIGHT_LINE, Displays.padding(0, 3, 3, 2, it)) }),
    ;

    override fun toString() = toFormattedName()
}
