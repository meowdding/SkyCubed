package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import com.teamresourceful.resourcefulconfigkt.api.ObjectKt
import net.minecraft.util.ARGB
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonDoorType
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonRoomType
import tech.thatgravyboat.skycubed.features.overlays.pickuplog.PickUpLogComponents
import tech.thatgravyboat.skycubed.features.tablist.CompactTablist
import tech.thatgravyboat.skycubed.features.tablist.CompactTablistSorting

// This is because we need to use the property delegate in the init block
private val EMPTY_PROPERTY = object {}

open class OverlayConfig(private val title: String) : ObjectKt(), Translatable {

    override fun getTranslationKey(): String = this.title
}

object InfoHudOverlayConfig : OverlayConfig("Edit Info Hud Overlay") {

    var enabled by boolean(true) {
        this.translation = "config.skycubed.overlays.info.enabled"
    }

}

object RpgOverlayConfig : OverlayConfig("Edit RPG Overlay") {

    var enabled by boolean(true) {
        this.translation = "config.skycubed.overlays.rpg.enabled"
    }

    var skyblockLevel by boolean(false) {
        this.translation = "config.skycubed.overlays.rpg.skyblockLevel"
    }
}

object TextOverlaysConfig : OverlayConfig("Edit Text Overlays") {

    var healthDisplay by enum<HealthDisplay>(HealthDisplay.NORMAL) {
        this.translation = "config.skycubed.overlays.healthDisplay"
    }

    var manaEnabled by boolean(true) {
        this.translation = "config.skycubed.overlays.manaEnabled"
    }

    var defenseEnabled by boolean(false) {
        this.translation = "config.skycubed.overlays.defenseEnabled"
    }
}

object SackOverlayConfig : OverlayConfig("Edit Sack Overlay") {
    var enabled by boolean(true) {
        this.translation = "config.skycubed.overlays.sacks.enabled"
    }

    var background by boolean(false) {
        this.translation = "config.skycubed.overlays.sacks.background"
    }

    var sackItems by strings {
        this.condition = { false }
    }
}

object TabListOverlayConfig : OverlayConfig("Edit Tab List Overlay") {

    var enabled by observable(
        boolean(true) {
            this.translation = "config.skycubed.overlays.tablist.enabled"
        },
    ) { _, new ->
        CompactTablist.onEnabledDisabled(new)
    }

    var sorting by observable(
        enum<CompactTablistSorting>(CompactTablistSorting.NORMAL) {
            this.translation = "config.skycubed.overlays.tablist.sorting"
        },
    ) { _, _ ->
        CompactTablist.onSortingUpdate()
    }
}

object MapOverlayConfig : OverlayConfig("Edit Map Overlay") {

    var enabled by boolean(false) {
        this.translation = "config.skycubed.overlays.map.enabled"
    }
}

object DungeonMapOverlayConfig : OverlayConfig("Edit Dungeon Map Overlay") {

    var enabled by boolean(false) {
        this.translation = "config.skycubed.overlays.dungeonmap.enabled"
    }

    init {
        separator {
            this.title = "config.skycubed.overlays.dungeonmap.roomColors"
            this.description = "config.skycubed.overlays.dungeonmap.roomColors.desc"
        }

        for (type in DungeonRoomType.entries) {
            val id = type.name.lowercase()
            observable(color("${id}_room", ARGB.opaque(type.defaultDisplayColor)) {
                this.presets = DungeonRoomType.DEFAULT_ROOM_COLORS
                this.translation = "config.skycubed.overlays.dungeonmap.roomColors.$id"
            }) { _, new ->
                type.displayColor = new
            }.provideDelegate(this, ::EMPTY_PROPERTY)
        }

        separator {
            this.title = "config.skycubed.overlays.dungeonmap.doorColors"
            this.description = "config.skycubed.overlays.dungeonmap.doorColors.desc"
        }

        for (type in DungeonDoorType.entries) {
            val id = type.name.lowercase()
            observable(color("${id}_door", ARGB.opaque(type.defaultDisplayColor)) {
                this.presets = DungeonRoomType.DEFAULT_ROOM_COLORS
                this.translation = "config.skycubed.overlays.dungeonmap.doorColors.$id"
            }) { _, new ->
                type.displayColor = new
            }.provideDelegate(this, ::EMPTY_PROPERTY)
        }
    }
}

object PickupLogOverlayConfig : OverlayConfig("Edit Pickup Log Overlay") {

    var enabled by boolean(true) {
        this.translation = "config.skycubed.overlays.pickuplog.enabled"
    }

    var compact by boolean(true) {
        this.translation = "config.skycubed.overlays.pickuplog.compact"
    }

    var time by int(5) {
        this.translation = "config.skycubed.overlays.pickuplog.time"
        this.range = 1..60
        this.slider = true
    }

    var sackItems by boolean(false) {
        this.translation = "config.skycubed.overlays.pickuplog.sackItems"
    }

    var appearance by draggable<PickUpLogComponents>(*PickUpLogComponents.entries.toTypedArray()) {
        this.translation = "config.skycubed.overlays.pickuplog.appearance"
    }
}

object CommissionOverlayConfig : OverlayConfig("Edit Commissions Overlay") {

    var enabled by boolean(true) {
        this.translation = "config.skycubed.overlays.commissions.enabled"
    }

    var format by boolean(true) {
        this.translation = "config.skycubed.overlays.commissions.format"
    }

    var background by boolean(false) {
        this.translation = "config.skycubed.overlays.commissions.background"
    }
}

object NpcOverlayConfig : OverlayConfig("Edit NPC Overlay") {

    var enabled by boolean(true) {
        this.translation = "config.skycubed.overlays.npc.enabled"
    }

    var durationPerMessage by float(2.5f) {
        this.translation = "config.skycubed.overlays.npc.durationPerMessage"
    }

    var durationForActionMessage by float(10f) {
        this.translation = "config.skycubed.overlays.npc.durationForActionMessage"
    }

    var hideChatMessage by boolean(true) {
        this.translation = "config.skycubed.overlays.npc.hideChatMessage"
    }
}
