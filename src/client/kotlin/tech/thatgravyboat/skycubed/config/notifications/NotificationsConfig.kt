package tech.thatgravyboat.skycubed.config.notifications

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object NotificationsConfig : CategoryKt("notifications") {

    override val name: TranslatableValue = Translated("skycubed.config.notifications")

    init {
        separator {
            this.title = "Hypixel"
            this.description = "Notifications for Hypixel events."
        }
    }

    val friendJoinLeave = obj("friendJoinLeave", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "skycubed.config.notifications.friend_join_leave"
    }

    val guildJoinLeave = obj("guildJoinLeave", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "skycubed.config.notifications.guild_join_leave"
    }

    init {
        separator {
            this.title = "Hoppity's Hunt"
            this.description = "Notifications for Hoppity's Hunt."
        }
    }

    val hoppityYouFound = obj("hoppityYouFound", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "skycubed.config.notifications.hoppity.you_found"
    }

    val hoppityEggAppeared = obj("hoppityEggAppeared", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "skycubed.config.notifications.hoppity.egg_appeared"
    }

    init {
        separator {
            this.title = "Mining"
            this.description = "Notifications for mining."
        }
    }

    val skymall = obj("skymall", NotificationObject(hideMessage = true, showAsToast = true, toastDuration = 10000)) {
        this.translation = "skycubed.config.notifications.skymall"
    }

    val monolith = obj("monolith", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "skycubed.config.notifications.monolith"
    }

    init {
        separator {
            this.title = "The Rift"
            this.description = "Notifications for The Rift."
        }
    }

    val riftOrb = obj("riftOrb", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "skycubed.config.notifications.rift_orb"
    }

    init {
        separator {
            this.title = "Miscellaneous"
            this.description = "Miscellaneous notifications."
        }
    }

    val blocksInTheWay = obj("blocksInTheWay", NotificationObject(hideMessage = true)) {
        this.translation = "skycubed.config.notifications.blocks_in_the_way"
    }

    val warping = obj("warping", NotificationObject(hideMessage = true)) {
        this.translation = "skycubed.config.notifications.warping"
    }

    val combo = obj("combo", NotificationObject(hideMessage = true, showAsToast = true, toastDuration = 1500)) {
        this.translation = "skycubed.config.notifications.combo"
    }

    val fishing = obj("fishing", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "skycubed.config.notifications.fishing"
    }

    val gifts = obj("gifts", NotificationObject(hideMessage = true, showAsToast = true)) {
        this.translation = "skycubed.config.notifications.gifts"
    }

}
