package tech.thatgravyboat.skycubed.config.screens

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object ScreensConfig : CategoryKt("screens") {

    override val name: TranslatableValue = Translated("skycubed.config.screens")

    var equipment by boolean(true) {
        this.translation = "skycubed.config.screens.equipment"
    }

    init {
        obj("wardrobe", WardrobeConfig) { this.translation = "skycubed.config.screens.wardrobe" }
    }
}
