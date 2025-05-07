package tech.thatgravyboat.skycubed.features.notifications

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.ui.UIIcons
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.screens.PauseScreen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenInitializedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentMatchResult
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.config.notifications.NotificationsConfig

@Module
object NotificationManager {

    private val notifications = listOf(
        NotificationType.single("friendJoinLeave", "Friend > (?<name>\\w{3,16}) (?<reason>joined|left)\\.", NotificationsConfig.friendJoinLeave) { _, match ->
            joinLeaveMessage(match, "Friends", TextColor.GREEN)
        },
        NotificationType.single("guildJoinLeave", "Guild > (?<name>\\w{3,16}) (?<reason>joined|left)\\.", NotificationsConfig.guildJoinLeave) { _, match ->
            joinLeaveMessage(match, "Guild", TextColor.DARK_GREEN)
        },

        NotificationType.single("warping", "(?:Warping|Sending to server mini76K|Evacuating to Your Island)\\.{3}|Warped to .*") { NotificationsConfig.warping },
        NotificationType.single("blocksInTheWay", "There are blocks in the way!") { NotificationsConfig.blocksInTheWay },

        NotificationType.unique("hoppityYouFound", "HOPPITY'S HUNT You found .*") { NotificationsConfig.hoppityYouFound },
        NotificationType.unique("hoppityEggAppeared", "HOPPITY'S HUNT A [\\w ]+ Egg has appeared!") { NotificationsConfig.hoppityEggAppeared },

        NotificationType.single("skymall1", "New buff: .*") { NotificationsConfig.skymall },
        NotificationType.unique("skymall2", "You can disable this messaging by toggling Sky Mall in your /hotm!") { NotificationsConfig.skymall.copy(showAsToast = false) },
        NotificationType.unique("skymall3", "New day! Your Sky Mall buff changed!") { NotificationsConfig.skymall.copy(showAsToast = false) }, // hide if new buff is hidden

        NotificationType.single("monolith", "MONOLITH! .*") { NotificationsConfig.monolith },

        NotificationType.single("rift_orb", "ORB! Picked up \\+25 Motes, recovered \\+2ф Rift Time!") { NotificationsConfig.riftOrb },

        NotificationType.single("combo", "\\+\\d+ Kill Combo .*|Your Kill Combo has expired!.*") { NotificationsConfig.combo },

        NotificationType.single("fishing", "TROPHY FISH! .*|GOOD CATCH! .*") { NotificationsConfig.fishing },

        NotificationType.single("gifts", "GIFT! .*") { NotificationsConfig.gifts },
    )

    private fun joinLeaveMessage(match: ComponentMatchResult, title: String, color: Int): Component = Text.multiline(
        Text.of(title) { this.color = color },
        Text.join(
            match["name"] ?: CommonText.EMPTY,
            CommonText.SPACE,
            Text.of("${match["reason"]?.stripped}.") { this.color = if (match["reason"]?.stripped == "joined") TextColor.GREEN else TextColor.RED }
        )
    )

    @Subscription
    fun onScreenInit(event: ScreenInitializedEvent) {
        if (event.screen is PauseScreen) {
            event.widgets.add(
                Widgets.button {
                    it.withSize(20, 20)
                    it.withPosition(McClient.window.guiScaledWidth - 25, 5)
                    it.withRenderer(WidgetRenderers.icon<Button>(UIIcons.BOX)
                        .withColor(MinecraftColors.DARK_GRAY)
                        .withCentered(14, 14)
                        .withPaddingBottom(2)
                    )
                    it.withTooltip(Text.of("Open SkyCubed Notifications"))
                    it.withCallback {
                        McClient.setScreen(NotificationsScreen())
                    }
                }
            )
        }
    }

    @Subscription(priority = Subscription.HIGHEST, receiveCancelled = true)
    fun onChatMessage(event: ChatReceivedEvent.Pre) {
        for (notification in notifications) {
            val config = notification.config()
            if (!config.shouldCheck()) continue
            val match = notification.regex.match(event.component) ?: continue
            if (config.showAsToast) {
                NotificationToast.add(
                    notification.id,
                    notification.factory(event.component, match),
                    config.toastDuration
                )
            }
            if (config.hideMessage) {
                event.cancel()
            }
            break
        }
    }
}
