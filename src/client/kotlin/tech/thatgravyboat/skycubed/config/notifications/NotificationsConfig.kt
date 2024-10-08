package tech.thatgravyboat.skycubed.config.notifications

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption

@Category("notifications")
object NotificationsConfig {

    @ConfigOption.Separator("Hypixel", description = "Notifications for Hypixel events.")

    @ConfigEntry(id = "friendJoinLeave", translation = "config.skycubed.notifications.friendJoinLeave")
    @Comment("", translation = "config.skycubed.notifications.friendJoinLeave.desc")
    val friendJoinLeave = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigEntry(id = "guildJoinLeave", translation = "config.skycubed.notifications.guildJoinLeave")
    @Comment("", translation = "config.skycubed.notifications.guildJoinLeave.desc")
    val guildJoinLeave = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigOption.Separator("Hoppity's Hunt", description = "Notifications for Hoppity's Hunt.")

    @ConfigEntry(id = "hoppityYouFound", translation = "config.skycubed.notifications.hoppityYouFound")
    @Comment("", translation = "config.skycubed.notifications.hoppityYouFound.desc")
    val hoppityYouFound = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigEntry(id = "hoppityEggAppeared", translation = "config.skycubed.notifications.hoppityEggAppeared")
    @Comment("", translation = "config.skycubed.notifications.hoppityEggAppeared.desc")
    val hoppityEggAppeared = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigOption.Separator("Mining", description = "Notifications for mining.")

    @ConfigEntry(id = "skymall", translation = "config.skycubed.notifications.skymall")
    @Comment("", translation = "config.skycubed.notifications.skymall.desc")
    val skymall = NotificationObject(hideMessage = true, showAsToast = true, toastDuration = 10000)

    @ConfigEntry(id = "monolith", translation = "config.skycubed.notifications.monolith")
    @Comment("", translation = "config.skycubed.notifications.monolith.desc")
    val monolith = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigOption.Separator("The Rift", description = "Notifications for The Rift.")

    @ConfigEntry(id = "riftOrb", translation = "config.skycubed.notifications.riftOrb")
    @Comment("", translation = "config.skycubed.notifications.riftOrb.desc")
    val riftOrb = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigOption.Separator("Miscellaneous", description = "Miscellaneous notifications.")

    @ConfigEntry(id = "blocksInTheWay", translation = "config.skycubed.notifications.blocksInTheWay")
    @Comment("", translation = "config.skycubed.notifications.blocksInTheWay.desc")
    val blocksInTheWay = NotificationObject(hideMessage = true)

    @ConfigEntry(id = "warping", translation = "config.skycubed.notifications.warping")
    @Comment("", translation = "config.skycubed.notifications.warping.desc")
    val warping = NotificationObject(hideMessage = true)

    @ConfigEntry(id = "combo", translation = "config.skycubed.notifications.combo")
    @Comment("", translation = "config.skycubed.notifications.combo.desc")
    val combo = NotificationObject(hideMessage = true, showAsToast = true)
}