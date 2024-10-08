package tech.thatgravyboat.skycubed.config.notifications

import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption

@ConfigObject
data class NotificationObject(

    @ConfigEntry(id = "hideChatMessage", translation = "config.skycubed.hideChatMessage")
    @Comment("", translation = "config.skycubed.hideChatMessage.desc")
    var hideMessage: Boolean = false,

    @ConfigOption.Separator(value = "Toast Options", description = "Options for toast notifications.")

    @ConfigEntry(id = "showAsToast", translation = "config.skycubed.showAsToast")
    @Comment("", translation = "config.skycubed.showAsToast.desc")
    var showAsToast: Boolean = false,

    @ConfigEntry(id = "toastDuration", translation = "config.skycubed.toastDuration")
    @Comment("", translation = "config.skycubed.toastDuration.desc")
    @ConfigOption.Range(min = 0.0, max = 30000.0)
    @ConfigOption.Slider
    var toastDuration: Int = 5000,
) {

    fun shouldCheck(): Boolean {
        return hideMessage || showAsToast
    }
}