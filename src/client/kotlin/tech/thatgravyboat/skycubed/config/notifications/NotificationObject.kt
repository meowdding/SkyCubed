package tech.thatgravyboat.skycubed.config.notifications

import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType

@ConfigObject
data class NotificationObject(

    @ConfigEntry(
        id = "hideChatMessage",
        type = EntryType.BOOLEAN
    )
    @Comment("Whether to hide notifications.")
    var hideMessage: Boolean = false,

    @ConfigOption.Separator(value = "Toast Options", description = "Options for toast notifications.")
    @ConfigEntry(
        id = "showAsToast",
        type = EntryType.BOOLEAN
    )
    @Comment("Whether to show notifications as a toast.")
    var showAsToast: Boolean = false,

    @ConfigEntry(
        id = "toastDuration",
        type = EntryType.INTEGER
    )
    @Comment("The duration of the toast in milliseconds.")
    @ConfigOption.Range(min = 0.0, max = 30000.0)
    @ConfigOption.Slider
    var toastDuration: Int = 5000,
) {

    fun shouldCheck(): Boolean {
        return hideMessage || showAsToast
    }
}