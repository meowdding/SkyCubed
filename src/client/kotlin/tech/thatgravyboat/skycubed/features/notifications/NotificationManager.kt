package tech.thatgravyboat.skycubed.features.notifications

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.config.notifications.NotificationsConfig

object NotificationManager {

    private val notifications = listOf(
        NotificationType.single("friendJoinLeave", "Friend > \\w{3,16} (joined|left)\\.") { NotificationsConfig.friendJoinLeave },
        NotificationType.single("guildJoinLeave", "Guild > \\w{3,16} (joined|left)\\.") { NotificationsConfig.guildJoinLeave },

        NotificationType.single("blocksInTheWay", "There are blocks in the way!") { NotificationsConfig.blocksInTheWay },

        NotificationType.unique("hoppityYouFound", "HOPPITY'S HUNT You found .*") { NotificationsConfig.hoppityYouFound },
        NotificationType.unique("hoppityEggAppeared", "HOPPITY'S HUNT A [\\w ]+ Egg has appeared!") { NotificationsConfig.hoppityEggAppeared },

        NotificationType.single("skymall", "New buff: .*") { NotificationsConfig.skymall },
        NotificationType.single("skymall", "New day! Your Sky Mall buff changed!") { NotificationsConfig.skymall.copy(showAsToast = false) }, // hide if new buff is hidden

        NotificationType.single("monolith", "MONOLITH! .*") { NotificationsConfig.monolith },

        NotificationType.single("rift_orb", "ORB! Picked up +25 Motes, recovered +2ф Rift Time!") { NotificationsConfig.riftOrb },
    )

    @Subscription(priority = Subscription.HIGHEST, receiveCancelled = true)
    fun onChatMessage(event: ChatReceivedEvent) {
        val message = event.text
        for (notification in notifications) {
            if (notification.matches(message)) {
                val config = notification.config()
                if (config.showAsToast) {
                    NotificationToast.add(notification.id, event.component, config.toastDuration)
                }
                if (config.hideMessage) {
                    SkyCubed.logger.info("[Cancelled] [CHAT] ${event.text}")
                    event.cancel()
                }
                break
            }
        }
    }
}