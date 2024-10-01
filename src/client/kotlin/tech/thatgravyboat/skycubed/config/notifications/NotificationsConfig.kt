package tech.thatgravyboat.skycubed.config.notifications

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType

@Category("notifications")
object NotificationsConfig {

    @ConfigOption.Separator("Hypixel")
    @ConfigEntry(id = "friendJoinLeave", type = EntryType.OBJECT)
    val friendJoinLeave = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigEntry(id = "friendJoinLeave", type = EntryType.OBJECT)
    val guildJoinLeave = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigOption.Separator("Hoppity's Hunt")
    @ConfigEntry(id = "hoppityYouFound", type = EntryType.OBJECT)
    val hoppityYouFound = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigEntry(id = "hoppityEggAppeared", type = EntryType.OBJECT)
    val hoppityEggAppeared = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigOption.Separator("Mining")
    @ConfigEntry(id = "skymall", type = EntryType.OBJECT)
    val skymall = NotificationObject(hideMessage = true, showAsToast = true, toastDuration = 10000)

    @ConfigEntry(id = "monolith", type = EntryType.OBJECT)
    val monolith = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigOption.Separator("The Rift")
    @ConfigEntry(id = "riftOrb", type = EntryType.OBJECT)
    val riftOrb = NotificationObject(hideMessage = true, showAsToast = true)

    @ConfigOption.Separator("Miscellaneous")
    @ConfigEntry(id = "blocksInTheWay", type = EntryType.OBJECT)
    val blocksInTheWay = NotificationObject(hideMessage = true)
}