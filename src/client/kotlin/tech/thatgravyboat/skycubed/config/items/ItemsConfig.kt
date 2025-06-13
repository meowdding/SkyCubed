package tech.thatgravyboat.skycubed.config.items

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object ItemsConfig : CategoryKt("items") {

    override val name: TranslatableValue = Translated("skycubed.config.items")

    var itembars by boolean(true) {
        this.translation = "skycubed.config.items.itembars"
    }

    var cooldowns by boolean(true) {
        this.translation = "skycubed.config.items.cooldowns"
    }
}
