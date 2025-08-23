package tech.thatgravyboat.skycubed.config.screens

import tech.thatgravyboat.skycubed.config.overlays.OverlayConfig
import tech.thatgravyboat.skycubed.features.equipment.wardobe.WardrobeScreen

object WardrobeConfig : OverlayConfig("Edit Wardrobe Config") {

    var enabled by boolean(true) {
        this.translation = "skycubed.config.screens.wardrobe.enabled"
    }

    var textured by boolean("textures", false) {
        this.translation = "skycubed.config.screens.wardrobe.textures"
    }

    var tooltipType by enum(WardrobeScreen.WardrobeTooltipType.MINIMAL) {
        this.translation = "skycubed.config.screens.wardrobe.tooltip_type"
    }
}
