package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import me.owdding.lib.overlays.ConfigPosition

object OverlayPositions : CategoryKt("positions") {

    override val hidden: Boolean = true

    val rpg = obj("rpg", ConfigPosition(x = 5, y = 5))
    val health = obj("health", ConfigPosition(x = 54, y = 16, scale = 0.5f))
    val mana = obj("mana", ConfigPosition(x = 54, y = 10, scale = 0.5f))
    val defense = obj("defense", ConfigPosition(x = 90, y = 3))
    val speed = obj("speed", ConfigPosition(x = 126, y = 3))
    val commissions = obj("commissions", ConfigPosition(x = 0, y = 100))
    val sack = obj("sack", ConfigPosition(x = -300, y = 10))
    val powerOrb = obj("powerOrb", ConfigPosition(x = -200, y = 50))
    val trophyFish = obj("trophyFish", ConfigPosition(x = -600, y = 10))
    val pickupLog = obj("pickupLog", ConfigPosition(x = 0, y = 150))
    val map = obj("map", ConfigPosition(x = -90, y = 0))
    val hotbar = obj("hotbar", ConfigPosition(x = 0, y = -22))
    val itemtext = obj("itemtext", ConfigPosition(x = 0, y = -35))
    val info = obj("info", ConfigPosition(x = 0, y = 0))
}
