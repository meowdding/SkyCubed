package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Range
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Slider
import com.teamresourceful.resourcefulconfig.api.types.info.Translatable

@Category(
    "overlays",
    categories = [
        OverlayPositions::class
    ]
)
object OverlaysConfig {

    @ConfigEntry(id = "rpgEnabled", translation = "config.skycubed.overlays.rpgEnabled")
    @Comment("", translation = "config.skycubed.overlays.rpgEnabled.desc")
    var rpgEnabled = true

    @ConfigEntry(id = "healthEnabled", translation = "config.skycubed.overlays.healthEnabled")
    @Comment("", translation = "config.skycubed.overlays.healthEnabled.desc")
    var healthDisplay = HealthDisplay.NORMAL

    @ConfigEntry(id = "manaEnabled", translation = "config.skycubed.overlays.manaEnabled")
    @Comment("", translation = "config.skycubed.overlays.manaEnabled.desc")
    var manaEnabled = true

    @ConfigEntry(id = "defenseEnabled", translation = "config.skycubed.overlays.defenseEnabled")
    @Comment("", translation = "config.skycubed.overlays.defenseEnabled.desc")
    var defenseEnabled = false

    @ConfigEntry(id = "commissions", translation = "config.skycubed.overlays.commissions")
    @Comment("", translation = "config.skycubed.overlays.commissions.desc")
    val commissions = CommissionOverlay()

    @Slider
    @Range(min = 0.0, max = 100.0)
    @ConfigEntry(id = "coldOverlay", translation = "config.skycubed.overlays.coldOverlay")
    @Comment("", translation = "config.skycubed.overlays.coldOverlay.desc")
    var coldOverlay = 80

    @ConfigEntry(id = "npcDialogue", translation = "config.skycubed.overlays.npcDialogue")
    @Comment("", translation = "config.skycubed.overlays.npcDialogue.desc")
    val npcDialogue = NpcOverlay()
}

enum class HealthDisplay : Translatable {
    DISABLED,
    NORMAL,
    EFFECTIVE;

    override fun getTranslationKey(): String = "config.skycubed.overlays.healthDisplay.${name.lowercase()}"
}