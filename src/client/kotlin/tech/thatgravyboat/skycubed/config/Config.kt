package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.annotations.Config
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Select
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidget
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement
import tech.thatgravyboat.skycubed.config.notifications.NotificationsConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig

@Config(
    "skycubed/config",
    categories = [
        OverlaysConfig::class,
        NotificationsConfig::class,
        ChatConfig::class,
    ]
)
object Config {

    @ConfigEntry(id = "hiddenActionBarWidgets")
    @Select("Hide")
    var hiddenActionBarWidgets: Array<ActionBarWidget> = arrayOf()

    @ConfigEntry(id = "hiddenHudElements")
    @Select("Hide")
    var hiddenHudElements: Array<HudElement> = arrayOf()

    @ConfigEntry(id = "equipmentSlots")
    var equipmentSlots: Boolean = true

    // TODO MOVE
    @ConfigEntry(id = "compactTablist")
    var compactTablist: Boolean = true
}