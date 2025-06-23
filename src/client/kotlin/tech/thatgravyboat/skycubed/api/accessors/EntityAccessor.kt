package tech.thatgravyboat.skycubed.api.accessors

import net.minecraft.world.entity.Entity

@Suppress("PropertyName")
interface EntityAccessor {

    var `skycubed$glow`: Boolean
    var `skycubed$glowColor`: Int

}

var Entity.glow
    get() = this.isCurrentlyGlowing
    set(value) {
        (this as? EntityAccessor)?.`skycubed$glow` = value
    }

var Entity.glowColor
    get() = this.teamColor
    set(value) {
        (this as? EntityAccessor)?.`skycubed$glowColor` = value
    }
