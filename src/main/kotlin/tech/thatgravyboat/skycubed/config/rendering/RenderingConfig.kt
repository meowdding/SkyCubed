package tech.thatgravyboat.skycubed.config.rendering

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object RenderingConfig : CategoryKt("render") {

    override val name: TranslatableValue = Translated("skycubed.config.rendering")

    var showOwnTag by boolean(false) {
        this.translation = "skycubed.config.rendering.show_own_tag"
    }

    val customDamage = obj("custom_damage", CustomDamageConfig) {
        this.translation = "skycubed.config.rendering.custom_damage"
    }
}
