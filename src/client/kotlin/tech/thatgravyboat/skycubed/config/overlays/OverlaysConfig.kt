package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Hidden
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType

@Category("overlays")
object OverlaysConfig {

    @Hidden
    @ConfigEntry(id = "rpg", type = EntryType.OBJECT)
    val rpg = Position(x = 5, y = 5)

    @ConfigEntry(id = "rpgEnabled", type = EntryType.BOOLEAN)
    var rpgEnabled = true

    @Hidden
    @ConfigEntry(id = "health", type = EntryType.OBJECT)
    val health = Position(x = 54, y = 16, scale = 0.5f)

    @ConfigEntry(id = "healthEnabled", type = EntryType.BOOLEAN)
    var healthEnabled = true

    @ConfigEntry(id = "showEffectiveHealth", type = EntryType.BOOLEAN)
    var showEffectiveHealth = false

    @Hidden
    @ConfigEntry(id = "mana", type = EntryType.OBJECT)
    val mana = Position(x = 54, y = 10, scale = 0.5f)

    @ConfigEntry(id = "manaEnabled", type = EntryType.BOOLEAN)
    var manaEnabled = true

    @Hidden
    @ConfigEntry(id = "defense", type = EntryType.OBJECT)
    val defense = Position(x = 90, y = 3)

    @ConfigEntry(id = "defenseEnabled", type = EntryType.BOOLEAN)
    var defenseEnabled = false
}