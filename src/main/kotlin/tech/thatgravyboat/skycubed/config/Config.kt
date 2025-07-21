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
import tech.thatgravyboat.skycubed.api.overlays.EditOverlaysScreen
import tech.thatgravyboat.skycubed.config.chat.ChatConfig
import tech.thatgravyboat.skycubed.config.items.ItemsConfig
import tech.thatgravyboat.skycubed.config.notifications.NotificationsConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.rendering.RenderingConfig
import tech.thatgravyboat.skycubed.config.screens.ScreensConfig
import tech.thatgravyboat.skycubed.features.notifications.NotificationsScreen
import tech.thatgravyboat.skycubed.features.screens.SackHudEditScreen

object Config : ConfigKt("skycubed/config") {

    override val name: TranslatableValue = Literal("SkyCubed (v${SkyCubed.mod.metadata.version.friendlyString})")
    override val description: TranslatableValue = Literal("SkyBlock UI overhaul mod.")
    override val links: Array<ResourcefulConfigLink> = arrayOf(
        ResourcefulConfigLink.create(
            "https://modrinth.com/project/skycubed",
            "modrinth",
            TranslatableValue("Modrinth", "skycubed.config.info.modrinth"),
        ),
        ResourcefulConfigLink.create(
            "https://github.com/ThatGravyBoat/SkyCubed",
            "code",
            TranslatableValue("GitHub", "skycubed.config.info.github"),
        ),
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

    val hiddenActionBarWidgets by select<ActionBarWidget> {
        this.translation = "skycubed.config.general.hidden_action_bar_widgets"
    }

    val hiddenHudElements by transform(
        select<HudElement> {
            this.translation = "skycubed.config.general.hidden_hud_elements"
        },
        { it.toTypedArray() },
        { it.toSet() },
    )

    init {
        separator {
            this.title = "Quick Access"
            this.description = "SkyCubed makes use of multiple screens for different things so they will be in this section."
        }

        button {
            this.title = "skycubed.config.general.keybinds"
            this.text = "Open"
            this.onClick {
                McClient.setScreen(KeyBindsScreen(McScreen.self, McClient.options))
            }
        }

        button {
            this.title = "skycubed.config.general.overlays"
            this.text = "Open"
            this.onClick {
                McClient.setScreen(EditOverlaysScreen(McScreen.self))
            }
        }


        button {
            this.title = "skycubed.config.general.notifications"
            this.text = "Open"
            this.onClick {
                McClient.setScreen(NotificationsScreen())
            }
        }

        button {
            this.title = "skycubed.config.general.sack_hud"
            this.text = "Open"
            this.onClick {
                McClient.setScreen(SackHudEditScreen())
            }
        }
    }
}
