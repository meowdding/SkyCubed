package tech.thatgravyboat.skycubed.config.screens

import tech.thatgravyboat.skycubed.config.overlays.Overlay

object WardrobeConfig : Overlay("Edit Wardrobe Config") {

    var enabled by boolean("enabled", true) {
        this.translation = "config.skycubed.screens.wardrobe.enabled"
    }

    var textured by boolean("textures", false) {
        this.translation = "config.skycubed.screens.wardrobe.textures"
    }
}