package tech.thatgravyboat.skycubed.config.items

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object ItemsConfig : CategoryKt("items") {

    override val name: TranslatableValue = Translated("config.skycubed.items.title")

    var itembars by boolean("itembars", true) {
        this.translation = "config.skycubed.items.itembars"
    }

    var cooldowns by boolean("cooldowns", true) {
        this.translation = "config.skycubed.items.cooldowns"
    }
}