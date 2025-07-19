package tech.thatgravyboat.skycubed.config.notifications

import com.teamresourceful.resourcefulconfigkt.api.ObjectKt

class NotificationObject(
    hideMessage: Boolean = true,
    showAsToast: Boolean = true,
    toastDuration: Int = 5000
) : ObjectKt() {

    var hideMessage by boolean("hideChatMessage", hideMessage) {
        this.translation = "skycubed.config.notifications.hide_chat_message"
    }

    var showAsToast by boolean(showAsToast) {
        this.translation = "skycubed.config.notifications.show_as_toast"
    }

    var toastDuration by int(toastDuration) {
        this.translation = "skycubed.config.notifications.toast_duration"
        this.range = 0..30000
        this.slider = true
    }

    fun shouldCheck(): Boolean {
        return hideMessage || showAsToast
    }

    fun copy(
        hideMessage: Boolean? = null,
        showAsToast: Boolean? = null,
        toastDuration: Int? = null
    ) : NotificationObject {
        return NotificationObject(
            hideMessage = hideMessage ?: this.hideMessage,
            showAsToast = showAsToast ?: this.showAsToast,
            toastDuration = toastDuration ?: this.toastDuration
        )
    }

}
