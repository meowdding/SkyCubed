package tech.thatgravyboat.skycubed.features.notifications

import net.minecraft.network.chat.Component
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skycubed.config.notifications.NotificationObject

data class NotificationType(
    val id: String?,
    val config: () -> NotificationObject,
    val regex: Regex,
    val factory: (Component, MatchGroupCollection) -> Component = { c, _ -> c }
) {

    fun matches(message: String): Boolean = config().shouldCheck() && regex.matches(message)

    companion object {

        fun unique(key: String, @Language("RegExp") regex: String, config: () -> NotificationObject): NotificationType {
            return NotificationType(null, config, Regex(regex))
        }

        fun unique(key: String, @Language("RegExp") regex: String, config: NotificationObject, factory: (Component, MatchGroupCollection) -> Component = { c, _ -> c }): NotificationType {
            return NotificationType(null, { config }, Regex(regex), factory)
        }

        fun single(key: String, @Language("RegExp") regex: String, config: () -> NotificationObject): NotificationType {
            return NotificationType(key, config, Regex(regex))
        }

        fun single(key: String, @Language("RegExp") regex: String, config: NotificationObject, factory: (Component, MatchGroupCollection) -> Component = { c, _ -> c }): NotificationType {
            return NotificationType(key, { config }, Regex(regex), factory)
        }
    }
}