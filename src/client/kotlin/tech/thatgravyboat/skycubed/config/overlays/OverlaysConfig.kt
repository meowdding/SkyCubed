package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Hidden
import com.teamresourceful.resourcefulconfig.api.types.info.Translatable

@Category("overlays")
object OverlaysConfig {

    @Hidden
    @ConfigEntry(id = "rpg")
    val rpg = Position(x = 5, y = 5)

    @ConfigEntry(id = "rpgEnabled", translation = "config.skycubed.overlays.rpgEnabled")
    @Comment("", translation = "config.skycubed.overlays.rpgEnabled.desc")
    var rpgEnabled = true

    @Hidden
    @ConfigEntry(id = "health")
    val health = Position(x = 54, y = 16, scale = 0.5f)

    @ConfigEntry(id = "healthEnabled", translation = "config.skycubed.overlays.healthEnabled")
    @Comment("", translation = "config.skycubed.overlays.healthEnabled.desc")
    var healthDisplay = HealthDisplay.NORMAL

    @Hidden @ConfigEntry(id = "mana")
    val mana = Position(x = 54, y = 10, scale = 0.5f)

    @ConfigEntry(id = "manaEnabled", translation = "config.skycubed.overlays.manaEnabled")
    @Comment("", translation = "config.skycubed.overlays.manaEnabled.desc")
    var manaEnabled = true

    @Hidden @ConfigEntry(id = "defense")
    val defense = Position(x = 90, y = 3)

    @ConfigEntry(id = "defenseEnabled", translation = "config.skycubed.overlays.defenseEnabled")
    @Comment("", translation = "config.skycubed.overlays.defenseEnabled.desc")
    var defenseEnabled = false
}

enum class HealthDisplay : Translatable {
    DISABLED,
    NORMAL,
    EFFECTIVE;

    override fun getTranslationKey(): String = "config.skycubed.overlays.healthDisplay.${name.lowercase()}"
}