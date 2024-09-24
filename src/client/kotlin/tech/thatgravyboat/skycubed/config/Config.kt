package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.annotations.Config
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType

@Config("skycubed")
object Config {

    @field:ConfigEntry(id = "rpg", type = EntryType.OBJECT)
    val rpg = Position(x = 5, y = 5)
}