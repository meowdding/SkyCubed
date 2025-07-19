package tech.thatgravyboat.skycubed.config.screens

import tech.thatgravyboat.skycubed.config.overlays.OverlayConfig

object WardrobeConfig : OverlayConfig("Edit Wardrobe Config") {

    var enabled by boolean(true) {
        this.translation = "skycubed.config.screens.wardrobe.enabled"
    }

    var textured by boolean("textures", false) {
        this.translation = "skycubed.config.screens.wardrobe.textures"
    }
}
