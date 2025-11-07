package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import com.teamresourceful.resourcefulconfigkt.api.ConfigDelegateProvider
import com.teamresourceful.resourcefulconfigkt.api.ObjectKt
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry
import com.teamresourceful.resourcefulconfigkt.api.RConfigKtEntry
import me.owdding.lib.displays.Alignment
import net.minecraft.util.ARGB
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonDoorType
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonRoomType
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.map.screen.MapShape
import tech.thatgravyboat.skycubed.features.overlays.map.MinimapOverlay
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
        this.translation = "skycubed.config.overlays.info.enabled"
    }

    var enabledLocations by select(*InfoLocation.entries.toTypedArray()) {
        this.translation = "skycubed.config.overlays.info.enabled_positions"
    }

}

object RpgOverlayConfig : OverlayConfig("Edit RPG Overlay") {

    var enabled by boolean(true) {
        this.translation = "skycubed.config.overlays.rpg.enabled"
    }

    var skyblockLevel by boolean(false) {
        this.translation = "skycubed.config.overlays.rpg.skyblock_level"
    }

    var playerDisplay by enum(PlayerDisplay.DISABLED) {
        this.translation = "skycubed.config.overlays.rpg.player_display"
        this.condition = { SkyCubed.is1218 }
    }
}

object TextOverlaysConfig : OverlayConfig("Edit Text Overlays") {

    var healthDisplay by enum<HealthDisplay>(HealthDisplay.NORMAL) {
        this.translation = "skycubed.config.overlays.health_display"
    }

    var manaEnabled by boolean(true) {
        this.translation = "skycubed.config.overlays.mana_enabled"
    }

    var defenseEnabled by boolean(false) {
        this.translation = "skycubed.config.overlays.defense_enabled"
    }

    var speedEnabled by boolean(false) {
        this.translation = "skycubed.config.overlays.speed_enabled"
    }
}

object SackOverlayConfig : OverlayConfig("Edit Sack Overlay") {
    var enabled by boolean(true) {
        this.translation = "skycubed.config.overlays.sacks.enabled"
    }

    var background by boolean(false) {
        this.translation = "skycubed.config.overlays.sacks.background"
    }

    var sackItems by strings {
        this.condition = { false }
    }
}

object TrophyFishOverlayConfig : OverlayConfig("Edit Trophy Fish Overlay") {

    var enabled by boolean(false) {
        this.translation = "skycubed.config.overlays.trophy_fish.enabled"
    }

    var background by boolean(false) {
        this.translation = "skycubed.config.overlays.trophy_fish.background"
    }

    var showNumbers by boolean(true) {
        this.translation = "skycubed.config.overlays.trophy_fish.show_numbers"
    }

    var showTotal by boolean(true) {
        this.translation = "skycubed.config.overlays.trophy_fish.show_total"
    }

    var hideUnlocked by boolean(false) {
        this.translation = "skycubed.config.overlays.trophy_fish.hide_unlocked"
    }

}

object TabListOverlayConfig : OverlayConfig("Edit Tab List Overlay") {

    var enabled by boolean(true) {
        this.translation = "skycubed.config.overlays.tablist.enabled"
    }.update()

    var sorting by enum(CompactTablistSorting.NORMAL) {
        this.translation = "skycubed.config.overlays.tablist.sorting"
    }.update()

    var backgroundColor by color(0xC0000000u.toInt()) {
        this.translation = "skycubed.config.overlays.tablist.background_color"
        this.allowAlpha = true
    }.update()

    var minColumns by int(3) {
        this.translation = "skycubed.config.overlays.tablist.min_columns"
        this.range = 1..9
    }.update()

    var maxColumns by int(5) {
        this.translation = "skycubed.config.overlays.tablist.max_columns"
        this.range = 1..9
    }.update()

    var targetColumnSize by int(20) {
        this.translation = "skycubed.config.overlays.tablist.target_column_size"
        this.range = 1..30
    }.update()

    private fun <T> ConfigDelegateProvider<RConfigKtEntry<T>>.update() = ObservableEntry(this) { _, _ -> CompactTablist.onToggle() }
}

object MapOverlayConfig : OverlayConfig("Edit Map Overlay") {

    var enabled by boolean(false) {
        this.translation = "skycubed.config.overlays.map.enabled"
    }

    var rotateAroundPlayer by boolean(false) {
        this.translation = "skycubed.config.overlays.map.rotate"
    }

    var mapShape by observable(
        enum(MapShape.SQUARE) {
            this.translation = "skycubed.config.overlays.map.shape"
        },
    ) { _, _ ->
        MinimapOverlay.updateDisplay()
    }

}

object DungeonMapOverlayConfig : OverlayConfig("Edit Dungeon Map Overlay") {

    var enabled by boolean(false) {
        this.translation = "skycubed.config.overlays.dungeonmap.enabled"
    }

    init {
        separator {
            this.title = "skycubed.config.overlays.dungeonmap.room_colors"
            this.description = "skycubed.config.overlays.dungeonmap.room_colors.desc"
        }

        for (type in DungeonRoomType.entries) {
            val id = type.name.lowercase()
            observable(
                color("${id}_room", ARGB.opaque(type.defaultDisplayColor)) {
                    this.presets = DungeonRoomType.DEFAULT_ROOM_COLORS
                    this.name = Translated("skycubed.config.overlays.dungeonmap.room_colors.$id")
                },
            ) { _, new ->
                type.displayColor = new
            }.provideDelegate(this, ::EMPTY_PROPERTY)
        }

        separator {
            this.title = "skycubed.config.overlays.dungeonmap.door_colors"
            this.description = "skycubed.config.overlays.dungeonmap.door_colors.desc"
        }

        for (type in DungeonDoorType.entries) {
            val id = type.name.lowercase()
            observable(
                color("${id}_door", ARGB.opaque(type.defaultDisplayColor)) {
                    this.presets = DungeonRoomType.DEFAULT_ROOM_COLORS
                    this.name = Translated("skycubed.config.overlays.dungeonmap.door_colors.$id")
                },
            ) { _, new ->
                type.displayColor = new
            }.provideDelegate(this, ::EMPTY_PROPERTY)
        }
    }
}

object PickupLogOverlayConfig : OverlayConfig("Edit Pickup Log Overlay") {

    var enabled by boolean(true) {
        this.translation = "skycubed.config.overlays.pickuplog.enabled"
    }

    var compact by boolean(true) {
        this.translation = "skycubed.config.overlays.pickuplog.compact"
    }

    var time by int(5) {
        this.translation = "skycubed.config.overlays.pickuplog.time"
        this.range = 1..60
        this.slider = true
    }

    var sackItems by boolean(false) {
        this.translation = "skycubed.config.overlays.pickuplog.sack_items"
    }

    var appearance by draggable<PickUpLogComponents>(*PickUpLogComponents.entries.toTypedArray()) {
        this.translation = "skycubed.config.overlays.pickuplog.appearance"
    }
}

object CommissionOverlayConfig : OverlayConfig("Edit Commissions Overlay") {

    var enabled by boolean(true) {
        this.translation = "skycubed.config.overlays.commissions.enabled"
    }

    var format by boolean(true) {
        this.translation = "skycubed.config.overlays.commissions.format"
    }

    var background by boolean(false) {
        this.translation = "skycubed.config.overlays.commissions.background"
    }
}

object NpcOverlayConfig : OverlayConfig("Edit NPC Overlay") {

    var enabled by boolean(true) {
        this.translation = "skycubed.config.overlays.npc.enabled"
    }

    var messageWordsPerMinute by int(180) {
        this.translation = "skycubed.config.overlays.npc.message_words_per_minute"
    }

    var minimumDurationPerMessage by float(0.75f) {
        this.translation = "skycubed.config.overlays.npc.message_minimum_duration"
    }

    var durationForActionMessage by float(10f) {
        this.translation = "skycubed.config.overlays.npc.duration_for_action_message"
    }

    var hideChatMessage by boolean(true) {
        this.translation = "skycubed.config.overlays.npc.hide_chat_message"
    }
}

object BossbarOverlayConfig : OverlayConfig("Vanilla Bossbar Overlay") {
    var removeWhenFull by boolean(false) {
        this.translation = "skycubed.config.overlays.bossbar.remove_bar_when_full"
    }

    var removeBarWhenObjective by boolean(false) {
        this.translation = "skycubed.config.overlays.bossbar.remove_bar_when_objective"
    }
}

object ItemTextOverlayConfig : OverlayConfig("Vanilla Item Text Overlay") {

    var moveable by boolean(false) {
        this.translation = "skycubed.config.overlays.itemtext.moveable"
    }

    var hidden by boolean(false) {
        this.translation = "skycubed.config.overlays.itemtext.hidden"
    }

    var alignment by enum(Alignment.CENTER) {
        this.translation = "skycubed.config.overlays.itemtext.alignment"
    }

    var color by color(0) {
        this.translation = "skycubed.config.overlays.itemtext.background_color"
        this.allowAlpha = true
    }

    var radius by int(0) {
        this.translation = "skycubed.config.overlays.itemtext.radius"
        this.range = 0..100
        this.slider = true
    }
}
