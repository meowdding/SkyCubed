package tech.thatgravyboat.skycubed.features.info

import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.base.DefaultBaseInfoOverlay
import tech.thatgravyboat.skycubed.features.info.base.DungeonBaseInfoOverlay
import tech.thatgravyboat.skycubed.features.info.base.InfoDisplayOverride
import tech.thatgravyboat.skycubed.features.info.base.RiftBaseInfoOverlay

// maybe TODO: element overrides
object BaseInfoDisplay {

    val BASE = SkyCubed.id("info/base")
    val RIGHT_LINE = SkyCubed.id("info/right")
    val LEFT_LINE = SkyCubed.id("info/left")

    private val islandOverrides: Map<SkyBlockIsland, InfoDisplayOverride> = mapOf(
        SkyBlockIsland.THE_RIFT to RiftBaseInfoOverlay,
        SkyBlockIsland.THE_CATACOMBS to DungeonBaseInfoOverlay,
    )

    private val currentOverride get() = islandOverrides[LocationAPI.island] ?: DefaultBaseInfoOverlay

    val baseDisplay = DisplayFactory.vertical {
        spacer(34, 5)
        display(Displays.center(34, 12, Displays.supplied { currentOverride.getIcon() }))
        spacer(34, 3)
        display(
            Displays.center(
                34,
                10,
                Displays.supplied { Displays.text(currentOverride.getText(), currentOverride::getTextColor) },
            ),
        )
        spacer(34, 1)
    }
}
