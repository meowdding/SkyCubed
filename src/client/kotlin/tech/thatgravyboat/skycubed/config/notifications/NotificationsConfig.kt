package tech.thatgravyboat.skycubed.config.notifications

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object NotificationsConfig : CategoryKt("notifications") {

    override val name: TranslatableValue = Translated("config.skycubed.notifications.title")

    init {
        separator {
            this.title = "Hypixel"
            this.description = "Notifications for Hypixel events."
        }
    }

    val friendJoinLeave = obj("friendJoinLeave", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "config.skycubed.notifications.friendJoinLeave"
    }

    val guildJoinLeave = obj("guildJoinLeave", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "config.skycubed.notifications.guildJoinLeave"
    }

    init {
        separator {
            this.title = "Hoppity's Hunt"
            this.description = "Notifications for Hoppity's Hunt."
        }
    }

    val hoppityYouFound = obj("hoppityYouFound", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "config.skycubed.notifications.hoppityYouFound"
    }

    val hoppityEggAppeared = obj("hoppityEggAppeared", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "config.skycubed.notifications.hoppityEggAppeared"
    }

    init {
        separator {
            this.title = "Mining"
            this.description = "Notifications for mining."
        }
    }

    val skymall = obj("skymall", NotificationObject(hideMessage = true, showAsToast = true, toastDuration = 10000)) {
        this.translation = "config.skycubed.notifications.skymall"
    }

    val monolith = obj("monolith", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "config.skycubed.notifications.monolith"
    }

    init {
        separator {
            this.title = "The Rift"
            this.description = "Notifications for The Rift."
        }
    }

    val riftOrb = obj("riftOrb", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "config.skycubed.notifications.riftOrb"
    }

    init {
        separator {
            this.title = "Miscellaneous"
            this.description = "Miscellaneous notifications."
        }
    }

    val blocksInTheWay = obj("blocksInTheWay", NotificationObject(hideMessage = true)) {
        this.translation = "config.skycubed.notifications.blocksInTheWay"
    }

    val warping = obj("warping", NotificationObject(hideMessage = true)) {
        this.translation = "config.skycubed.notifications.warping"
    }

    val combo = obj("combo", NotificationObject(hideMessage = true, showAsToast = true, toastDuration = 1500)) {
        this.translation = "config.skycubed.notifications.combo"
    }

    val fishing = obj("fishing", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "config.skycubed.notifications.fishing"
    }

    val gifts = obj("gifts", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "config.skycubed.notifications.gifts"
    }

}
