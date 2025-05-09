package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object OverlaysConfig : CategoryKt("overlays") {

    override val name: TranslatableValue = Translated("config.skycubed.overlays.title")

    init {
        category(OverlayPositions)

        obj("info", InfoHudOverlay) { this.translation = "config.skycubed.overlays.info" }
        obj("rpg", RpgOverlay) { this.translation = "config.skycubed.overlays.rpg" }
        obj("text", TextOverlays) { this.translation = "config.skycubed.overlays.text" }
        obj("tablist", TabListOverlay) { this.translation = "config.skycubed.overlays.tablist" }
        obj("sack", SackOverlayConfig) { this.translation = "config.skycubed.overlays.sacks" }
        obj("map", MapOverlay) { this.translation = "config.skycubed.overlays.map" }
        obj("dungeonmap", DungeonMapOverlayConfig) { this.translation = "config.skycubed.overlays.dungeonmap" }
        obj("pickupLog", PickupLogOverlay) { this.translation = "config.skycubed.overlays.pickuplog" }
        obj("commissions", CommissionOverlay) { this.translation = "config.skycubed.overlays.commissions" }
        obj("npc", NpcOverlay) { this.translation = "config.skycubed.overlays.npc" }
    }

    var coldOverlay by int("coldOverlay", 80) {
        this.translation = "config.skycubed.overlays.coldOverlay"
        this.range = 0..99
    }

    var movableHotbar by boolean("movableHotbar", false) {
        this.translation = "config.skycubed.overlays.movableHotbar"
    }

    var windOverlay by boolean("windOverlay", false) {
        this.translation = "config.skycubed.overlays.windOverlay"
    }
}

enum class HealthDisplay : Translatable {
    DISABLED,
    NORMAL,
    EFFECTIVE;

    override fun getTranslationKey(): String = "config.skycubed.overlays.healthDisplay.${name.lowercase()}"
}
