package tech.thatgravyboat.skycubed.config.rendering

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object RenderingConfig : CategoryKt("render") {

    override val name: TranslatableValue = Translated("config.skycubed.render.title")

    var showOwnTag by boolean(false) {
        this.translation = "config.skycubed.render.showOwnTag"
    }
}
