package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import com.teamresourceful.resourcefulconfigkt.api.ObjectKt
import tech.thatgravyboat.skycubed.features.overlays.pickuplog.PickUpLogComponents
import tech.thatgravyboat.skycubed.features.tablist.CompactTablist
import tech.thatgravyboat.skycubed.features.tablist.CompactTablistSorting

open class Overlay(private val title: String) : ObjectKt(), Translatable {

    override fun getTranslationKey(): String = this.title
}

object InfoHudOverlay : Overlay("Edit Info Hud Overlay") {

    var enabled by boolean("enabled", true) {
        this.translation = "config.skycubed.overlays.info.enabled"
    }

}

object RpgOverlay : Overlay("Edit RPG Overlay") {

    var enabled by boolean("enabled", true) {
        this.translation = "config.skycubed.overlays.rpg.enabled"
    }

    var skyblockLevel by boolean("skyblockLevel", false) {
        this.translation = "config.skycubed.overlays.rpg.skyblockLevel"
    }
}

object TextOverlays : Overlay("Edit Text Overlays") {

    var healthDisplay by enum<HealthDisplay>("healthDisplay", HealthDisplay.NORMAL) {
        this.translation = "config.skycubed.overlays.healthDisplay"
    }

    var manaEnabled by boolean("manaEnabled", true) {
        this.translation = "config.skycubed.overlays.manaEnabled"
    }

    var defenseEnabled by boolean("defenseEnabled", false) {
        this.translation = "config.skycubed.overlays.defenseEnabled"
    }
}

object SackOverlayConfig : Overlay("Edit Sack Overlay") {
    var enabled by boolean(true) {
        this.translation = "config.skycubed.overlays.sacks.enabled"
    }

    var background by boolean(false) {
        this.translation = "config.skycubed.overlays.sacks.background"
    }

    var sackItems by strings() {
        this.condition = { false }
    }
}

object TabListOverlay : Overlay("Edit Tab List Overlay") {

    var enabled by observable(boolean("enabled", true) {
        this.translation = "config.skycubed.overlays.tablist.enabled"
    }) { _, new ->
        CompactTablist.onEnabledDisabled(new)
    }

    var sorting by observable(enum<CompactTablistSorting>("sorting", CompactTablistSorting.NORMAL) {
        this.translation = "config.skycubed.overlays.tablist.sorting"
    }) { _, _ ->
        CompactTablist.onSortingUpdate()
    }
}

object MapOverlay : Overlay("Edit Map Overlay") {

    var enabled by boolean("enabled", false) {
        this.translation = "config.skycubed.overlays.map.enabled"
    }

    var dungeonMap by boolean("dungeonMap", true) {
        this.translation = "config.skycubed.overlays.map.dungeonMap"
    }
}

object PickupLogOverlay : Overlay("Edit Pickup Log Overlay") {

    var enabled by boolean("enabled", true) {
        this.translation = "config.skycubed.overlays.pickuplog.enabled"
    }

    var compact by boolean("compact", true) {
        this.translation = "config.skycubed.overlays.pickuplog.compact"
    }

    var time by int("time", 5) {
        this.translation = "config.skycubed.overlays.pickuplog.time"
        this.range = 1..30
    }

    var appearance by draggable<PickUpLogComponents>("appearance", *PickUpLogComponents.entries.toTypedArray()) {
        this.translation = "config.skycubed.overlays.pickuplog.appearance"
    }
}

object CommissionOverlay : Overlay("Edit Commissions Overlay") {

    var enabled by boolean("enabled", true) {
        this.translation = "config.skycubed.overlays.commissions.enabled"
    }

    var format by boolean("format", true) {
        this.translation = "config.skycubed.overlays.commissions.format"
    }

    var background by boolean("background", false) {
        this.translation = "config.skycubed.overlays.commissions.background"
    }
}

object NpcOverlay : Overlay("Edit NPC Overlay") {

    var enabled by boolean("enabled", true) {
        this.translation = "config.skycubed.overlays.npc.enabled"
    }

    var durationPerMessage by float("durationPerMessage", 2.5f) {
        this.translation = "config.skycubed.overlays.npc.durationPerMessage"
    }

    var durationForActionMessage by float("durationForActionMessage", 10f) {
        this.translation = "config.skycubed.overlays.npc.durationForActionMessage"
    }

    var hideChatMessage by boolean("hideChatMessage", true) {
        this.translation = "config.skycubed.overlays.npc.hideChatMessage"
    }
}
