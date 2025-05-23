package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object OverlayPositions : CategoryKt("positions") {

    override val hidden: Boolean = true

    val rpg = obj("rpg", Position(x = 5, y = 5)) {
        this.translation = "config.skycubed.positions.rpg"
    }

    val health = obj("health", Position(x = 54, y = 16, scale = 0.5f)) {
        this.translation = "config.skycubed.positions.health"
    }

    val mana = obj("mana", Position(x = 54, y = 10, scale = 0.5f)) {
        this.translation = "config.skycubed.positions.mana"
    }

    val defense = obj("defense", Position(x = 90, y = 3)) {
        this.translation = "config.skycubed.positions.defense"
    }

    val commissions = obj("commissions", Position(x = 0, y = 100)) {
        this.translation = "config.skycubed.positions.commissions"
    }

    val sack = obj("sack", Position(x = -300, y = 10)) {
        this.translation = "config.skycubed.positions.sack"
    }

    val trophyFish = obj("trophyFish", Position(x = -600, y = 10)) {
        this.translation = "config.skycubed.positions.trophyFish"
    }

    val pickupLog = obj("pickupLog", Position(x = 0, y = 150)) {
        this.translation = "config.skycubed.positions.pickupLog"
    }

    val map = obj("map", Position(x = -90, y = 0)) {
        this.translation = "config.skycubed.positions.map"
    }

    val hotbar = obj("hotbar", Position(x = 0, y = -22)) {
        this.translation = "config.skycubed.positions.hotbar"
    }
}
