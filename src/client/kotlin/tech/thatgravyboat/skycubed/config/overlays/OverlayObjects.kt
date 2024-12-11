package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Range
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Slider
import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import tech.thatgravyboat.skycubed.features.overlays.pickuplog.PickUpLogComponents

@ConfigObject
class RpgOverlay : Translatable {

    @ConfigEntry(id = "enabled", translation = "config.skycubed.overlays.rpg.enabled")
    @Comment("", translation = "config.skycubed.overlays.rpg.enabled.desc")
    var enabled: Boolean = true

    override fun getTranslationKey(): String = "Edit RPG Overlay"
}

@ConfigObject
class TextOverlays : Translatable {

    @ConfigEntry(id = "healthEnabled", translation = "config.skycubed.overlays.healthEnabled")
    @Comment("", translation = "config.skycubed.overlays.healthEnabled.desc")
    var healthDisplay: HealthDisplay = HealthDisplay.NORMAL

    @ConfigEntry(id = "manaEnabled", translation = "config.skycubed.overlays.manaEnabled")
    @Comment("", translation = "config.skycubed.overlays.manaEnabled.desc")
    var manaEnabled: Boolean = true

    @ConfigEntry(id = "defenseEnabled", translation = "config.skycubed.overlays.defenseEnabled")
    @Comment("", translation = "config.skycubed.overlays.defenseEnabled.desc")
    var defenseEnabled: Boolean = false

    override fun getTranslationKey(): String = "Edit Text Overlays"
}

@ConfigObject
class MapOverlay : Translatable {

    @ConfigEntry(id = "enabled", translation = "config.skycubed.overlays.map.enabled")
    @Comment("", translation = "config.skycubed.overlays.map.enabled.desc")
    var enabled: Boolean = false

    override fun getTranslationKey(): String = "Edit Map Overlay"
}

@ConfigObject
class PickupLogOverlay : Translatable {

    @ConfigEntry(id = "enabled", translation = "config.skycubed.overlays.pickuplog.enabled")
    @Comment("", translation = "config.skycubed.overlays.pickuplog.enabled.desc")
    var enabled: Boolean = true

    @ConfigEntry(id = "compact", translation = "config.skycubed.overlays.pickuplog.compact")
    @Comment("", translation = "config.skycubed.overlays.pickuplog.compact.desc")
    var compact: Boolean = true

    @ConfigEntry(id = "time", translation = "config.skycubed.overlays.pickuplog.time")
    @Comment("", translation = "config.skycubed.overlays.pickuplog.time.desc")
    @Range(min = 1.0, max = 30.0)
    var time: Int = 5

    @ConfigEntry(id = "appearance", translation = "config.skycubed.overlays.pickuplog.appearance")
    @Comment("", translation = "config.skycubed.overlays.pickuplog.appearance.desc")
    @ConfigOption.Draggable
    var appearance: Array<PickUpLogComponents> = PickUpLogComponents.entries.toTypedArray()

    override fun getTranslationKey(): String = "Edit Pickup Log Overlay"
}

@ConfigObject
class CommissionOverlay : Translatable {

    @ConfigEntry(id = "enabled", translation = "config.skycubed.overlays.commissions.enabled")
    @Comment("", translation = "config.skycubed.overlays.commissions.enabled.desc")
    var enabled: Boolean = true

    @ConfigEntry(id = "format", translation = "config.skycubed.overlays.commissions.format")
    @Comment("", translation = "config.skycubed.overlays.commissions.format.desc")
    var format: Boolean = true

    @ConfigEntry(id = "background", translation = "config.skycubed.overlays.commissions.background")
    @Comment("", translation = "config.skycubed.overlays.commissions.background.desc")
    var background: Boolean = false

    override fun getTranslationKey(): String = "Edit Commissions Overlay"
}

@ConfigObject
class NpcOverlay : Translatable {

    @ConfigEntry(id = "enabled", translation = "config.skycubed.overlays.npc.enabled")
    @Comment("", translation = "config.skycubed.overlays.npc.enabled.desc")
    var enabled: Boolean = false

    @ConfigEntry(id = "durationPerMessage", translation = "config.skycubed.overlays.npc.durationPerMessage")
    @Comment("", translation = "config.skycubed.overlays.npc.durationPerMessage.desc")
    var durationPerMessage: Float = 2.5f

    @ConfigEntry(id = "durationForActionMessage", translation = "config.skycubed.overlays.npc.durationForActionMessage")
    @Comment("", translation = "config.skycubed.overlays.npc.durationForActionMessage.desc")
    var durationForActionMessage: Float = 10f

    @ConfigEntry(id = "radius", translation = "config.skycubed.overlays.npc.radius")
    @Comment("", translation = "config.skycubed.overlays.npc.radius.desc")
    @Slider @Range(min = 0.0, max = 50.0)
    var overlayRadius: Int = 0

    override fun getTranslationKey(): String = "Edit NPC Overlay"
}