package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object OverlaysConfig : CategoryKt("overlays") {

    override val name: TranslatableValue = Translated("config.skycubed.overlays.title")

    init {
        category(OverlayPositions)

        obj("info", InfoHudOverlayConfig) { this.translation = "config.skycubed.overlays.info" }
        obj("rpg", RpgOverlayConfig) { this.translation = "config.skycubed.overlays.rpg" }
        obj("text", TextOverlaysConfig) { this.translation = "config.skycubed.overlays.text" }
        obj("tablist", TabListOverlayConfig) { this.translation = "config.skycubed.overlays.tablist" }
        obj("sack", SackOverlayConfig) { this.translation = "config.skycubed.overlays.sacks" }
        obj("trophyFish", TrophyFishOverlayConfig) { this.translation = "config.skycubed.overlays.trophyFish" }
        obj("map", MapOverlayConfig) { this.translation = "config.skycubed.overlays.map" }
        obj("dungeonmap", DungeonMapOverlayConfig) { this.translation = "config.skycubed.overlays.dungeonmap" }
        obj("pickupLog", PickupLogOverlayConfig) { this.translation = "config.skycubed.overlays.pickuplog" }
        obj("commissions", CommissionOverlayConfig) { this.translation = "config.skycubed.overlays.commissions" }
        obj("npc", NpcOverlayConfig) { this.translation = "config.skycubed.overlays.npc" }
    }

    var coldOverlay by int(80) {
        this.translation = "config.skycubed.overlays.coldOverlay"
        this.range = 0..99
    }

    var movableHotbar by boolean(false) {
        this.translation = "config.skycubed.overlays.movableHotbar"
    }

    var windOverlay by boolean(false) {
        this.translation = "config.skycubed.overlays.windOverlay"
    }
}

enum class HealthDisplay : Translatable {
    DISABLED,
    NORMAL,
    EFFECTIVE;

    override fun getTranslationKey(): String = "config.skycubed.overlays.healthDisplay.${name.lowercase()}"
}
