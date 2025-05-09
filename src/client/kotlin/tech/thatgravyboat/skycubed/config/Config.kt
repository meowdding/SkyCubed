package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.types.info.ResourcefulConfigLink
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidget
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.config.chat.ChatConfig
import tech.thatgravyboat.skycubed.config.items.ItemsConfig
import tech.thatgravyboat.skycubed.config.notifications.NotificationsConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.rendering.RenderingConfig
import tech.thatgravyboat.skycubed.config.screens.ScreensConfig
import tech.thatgravyboat.skycubed.features.notifications.NotificationsScreen

object Config : ConfigKt("skycubed/config") {

    override val name: TranslatableValue = Literal("SkyCubed (v${SkyCubed.mod.metadata.version.friendlyString})")
    override val description: TranslatableValue = Literal("SkyBlock UI overhaul mod.")
    override val links: Array<ResourcefulConfigLink> = arrayOf(
        ResourcefulConfigLink.create(
            "https://modrinth.com/project/skycubed",
            "modrinth",
            TranslatableValue("Modrinth", "config.info.skycubed.modrinth")
        ),
        ResourcefulConfigLink.create(
            "https://github.com/ThatGravyBoat/SkyCubed",
            "code",
            TranslatableValue("GitHub", "config.info.skycubed.github")
        )
    )

    init {
        category(OverlaysConfig)
        category(ScreensConfig)
        category(ItemsConfig)
        category(ChatConfig)
        category(NotificationsConfig)
        category(RenderingConfig)
    }

    init {
        separator {
            this.title = "General"
            this.description = "General options for the mod that dont fit into a specific category."
        }
    }

    val hiddenActionBarWidgets by select<ActionBarWidget>("hiddenActionBarWidgets") {
        this.translation = "config.skycubed.general.hiddenActionBarWidgets"
    }

    val hiddenHudElements by select<HudElement>("hiddenHudElements") {
        this.translation = "config.skycubed.general.hiddenHudElements"
    }

    init {
        separator {
            this.title = "Quick Access"
            this.description = "SkyCubed makes use of multiple screens for different things so they will be in this section."
        }

        button {
            this.title = "config.skycubed.general.keybinds"
            this.text = "Open"
            this.onClick {
                McClient.setScreen(KeyBindsScreen(McScreen.self, McClient.options))
            }
        }

        button {
            this.title = "config.skycubed.general.notifications"
            this.text = "Open"
            this.onClick {
                McClient.setScreen(NotificationsScreen())
            }
        }
    }
}