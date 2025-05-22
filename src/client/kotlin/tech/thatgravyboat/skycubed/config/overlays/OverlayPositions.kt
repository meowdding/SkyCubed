package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object OverlayPositions : CategoryKt("positions") {

    override val hidden: Boolean = true

    val rpg = obj("rpg", Position(x = 5, y = 5))
    val health = obj("health", Position(x = 54, y = 16, scale = 0.5f))
    val mana = obj("mana", Position(x = 54, y = 10, scale = 0.5f))
    val defense = obj("defense", Position(x = 90, y = 3))
    val commissions = obj("commissions", Position(x = 0, y = 100))
    val sack = obj("sack", Position(x = -300, y = 10))
    val pickupLog = obj("pickupLog", Position(x = 0, y = 150))
    val map = obj("map", Position(x = -90, y = 0))
    val hotbar = obj("hotbar", Position(x = 0, y = -22))
    val info = obj("info", Position(x = 0, y = 0))
}
