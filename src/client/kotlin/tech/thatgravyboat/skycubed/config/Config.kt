package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.annotations.Config
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Select
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidget
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement
import tech.thatgravyboat.skycubed.config.notifications.NotificationsConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig

@Config(
    "skycubed/config",
    categories = [
        OverlaysConfig::class,
        NotificationsConfig::class
    ]
)
object Config {

    @ConfigEntry(id = "hiddenActionBarWidgets", type = EntryType.ENUM)
    @Select("Hide")
    var hiddenActionBarWidgets: Array<ActionBarWidget> = arrayOf()

    @ConfigEntry(id = "hiddenHudElements", type = EntryType.ENUM)
    @Select("Hide")
    var hiddenHudElements: Array<HudElement> = arrayOf()
}