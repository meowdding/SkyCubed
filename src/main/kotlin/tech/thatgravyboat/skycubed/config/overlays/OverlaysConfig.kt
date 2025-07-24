package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object OverlaysConfig : CategoryKt("overlays") {

    override val name: TranslatableValue = Translated("skycubed.config.overlays")

    init {
        category(OverlayPositions)

        obj("info", InfoHudOverlayConfig) { this.translation = "skycubed.config.overlays.info" }
        obj("rpg", RpgOverlayConfig) { this.translation = "skycubed.config.overlays.rpg" }
        obj("text", TextOverlaysConfig) { this.translation = "skycubed.config.overlays.text" }
        obj("tablist", TabListOverlayConfig) { this.translation = "skycubed.config.overlays.tablist" }
        obj("sack", SackOverlayConfig) { this.translation = "skycubed.config.overlays.sacks" }
        obj("trophyFish", TrophyFishOverlayConfig) { this.translation = "skycubed.config.overlays.trophy_fish" }
        obj("map", MapOverlayConfig) { this.translation = "skycubed.config.overlays.map" }
        obj("dungeonmap", DungeonMapOverlayConfig) { this.translation = "skycubed.config.overlays.dungeonmap" }
        obj("pickupLog", PickupLogOverlayConfig) { this.translation = "skycubed.config.overlays.pickuplog" }
        obj("commissions", CommissionOverlayConfig) { this.translation = "skycubed.config.overlays.commissions" }
        obj("npc", NpcOverlayConfig) { this.translation = "skycubed.config.overlays.npc" }
        obj("bossbar", BossbarOverlayConfig) { this.translation = "skycubed.config.overlays.bossbar" }
        obj("itemtext", ItemTextOverlayConfig) { this.translation = "skycubed.config.overlays.itemtext" }
    }

    var coldOverlay by int(80) {
        this.translation = "skycubed.config.overlays.cold_overlay"
        this.range = 0..99
    }

    var movableHotbar by boolean(false) {
        this.translation = "skycubed.config.overlays.movable_hotbar"
    }

    var windOverlay by boolean(false) {
        this.translation = "skycubed.config.overlays.wind_overlay"
    }
}

enum class HealthDisplay : Translatable {
    DISABLED,
    NORMAL,
    EFFECTIVE;

    override fun getTranslationKey(): String = "skycubed.config.overlays.health.${name.lowercase()}"
}

enum class PlayerDisplay : Translatable {
    DISABLED,
    ARMORED,
    UNARMORED;

    override fun getTranslationKey(): String = "skycubed.config.overlays.rpg.player_display.${name.lowercase()}"
}
