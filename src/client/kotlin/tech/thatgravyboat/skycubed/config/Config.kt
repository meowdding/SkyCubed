package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.annotations.*
import com.teamresourceful.resourcefulconfig.api.annotations.Config
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Select
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Separator
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidget
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skycubed.config.chat.ChatConfig
import tech.thatgravyboat.skycubed.config.items.ItemsConfig
import tech.thatgravyboat.skycubed.config.notifications.NotificationsConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.screens.ScreensConfig
import tech.thatgravyboat.skycubed.features.notifications.NotificationsScreen

@Config(
    "skycubed/config",
    categories = [
        OverlaysConfig::class,
        ScreensConfig::class,
        ItemsConfig::class,
        ChatConfig::class,
        NotificationsConfig::class,
    ]
)
@ConfigInfo.Provider(ConfigInfoProvider::class)
object Config {

    @Separator("General", description = "General options for the mod that dont fit into a specific category.")

    @ConfigEntry(id = "hiddenActionBarWidgets", translation = "config.skycubed.general.hiddenActionBarWidgets")
    @Comment("", translation = "config.skycubed.general.hiddenActionBarWidgets.desc")
    @Select("Hide")
    var hiddenActionBarWidgets: Array<ActionBarWidget> = arrayOf()

    @ConfigEntry(id = "hiddenHudElements", translation = "config.skycubed.general.hiddenHudElements")
    @Comment("", translation = "config.skycubed.general.hiddenHudElements.desc")
    @Select("Hide")
    var hiddenHudElements: Array<HudElement> = arrayOf()

    @Separator("Quick Access", description = "SkyCubed makes use of multiple screens for different things so they will be in this section.")

    @ConfigButton(title = "config.skycubed.general.keybinds", text = "Open")
    @Comment("config.skycubed.general.keybinds.desc")
    val ignored1 = { McClient.setScreen(KeyBindsScreen(McScreen.self, McClient.self.options))}

    @ConfigButton(title = "config.skycubed.general.notifications", text = "Open")
    @Comment("config.skycubed.general.notifications.desc")
    val ignored2 = { McClient.setScreen(NotificationsScreen())}
}