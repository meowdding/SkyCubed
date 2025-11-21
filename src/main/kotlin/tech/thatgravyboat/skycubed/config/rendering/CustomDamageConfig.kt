package tech.thatgravyboat.skycubed.config.rendering

import com.teamresourceful.resourcefulconfigkt.api.ObjectKt
import tech.thatgravyboat.skycubed.features.misc.IconMode

object CustomDamageConfig : ObjectKt() {
    var enabled by boolean(false) {
        this.translation = "skycubed.config.rendering.custom_damage.enabled"
    }

    var shadow by boolean(true) {
        this.translation = "skycubed.config.rendering.custom_damage.shadow"
    }

    var combining by boolean(true) {
        this.translation = "skycubed.config.rendering.custom_damage.combining"
    }

    var combineThreshold by float(2F) {
        range = 0F..4F
        slider = true
        this.translation = "skycubed.config.rendering.custom_damage.combineThreshold"
    }

    var touchiness by int(1) {
        this.translation = "skycubed.config.rendering.custom_damage.touchiness"
        this.range = 1..3
    }

    var timeout by long(3000) {
        this.translation = "skycubed.config.rendering.custom_damage.timeout"
    }

    var fullTimeout by long(10000) {
        this.translation = "skycubed.config.rendering.custom_damage.fullTimeout"
    }

    var droppingTags by boolean(true) {
        this.translation = "skycubed.config.rendering.custom_damage.droppingTags"
    }

    var fadingTags by boolean(true) {
        this.translation = "skycubed.config.rendering.custom_damage.fadingTags"
    }

    var icons by enum(IconMode.BOTH) {
        this.translation = "skycubed.config.rendering.custom_damage.icons"
    }
}
