package tech.thatgravyboat.skycubed.features.notifications

import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skycubed.config.notifications.NotificationObject

data class NotificationType(
    val id: String?,
    val config: () -> NotificationObject,
    val regex: Regex
) {

    fun matches(message: String): Boolean = config().shouldCheck() && regex.matches(message)

    companion object {

        fun unique(key: String, @Language("RegExp") regex: String, config: () -> NotificationObject): NotificationType {
            return NotificationType(null, config, Regex(regex))
        }

        fun single(key: String, @Language("RegExp") regex: String, config: () -> NotificationObject): NotificationType {
            return NotificationType(key, config, Regex(regex))
        }
    }
}