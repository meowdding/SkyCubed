package tech.thatgravyboat.skycubed.config.screens

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object ScreensConfig : CategoryKt("screens") {

    override val name: TranslatableValue = Translated("config.skycubed.screens.title")

    var equipment by boolean("equipment", true) {
        this.translation = "config.skycubed.screens.equipment"
    }

    init {
        obj("wardrobe", WardrobeConfig) { this.translation = "config.skycubed.screens.wardrobe" }
    }
}