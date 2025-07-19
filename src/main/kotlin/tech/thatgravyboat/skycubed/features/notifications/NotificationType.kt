package tech.thatgravyboat.skycubed.features.notifications

import net.minecraft.network.chat.Component
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentMatchResult
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skycubed.config.notifications.NotificationObject

data class NotificationType(
    val id: String?,
    val config: () -> NotificationObject,
    val regex: ComponentRegex,
    val factory: (Component, ComponentMatchResult) -> Component = { c, _ -> c }
) {

    companion object {

        fun unique(key: String, @Language("RegExp") regex: String, config: () -> NotificationObject): NotificationType {
            return NotificationType(null, config, ComponentRegex(regex))
        }

        fun unique(key: String, @Language("RegExp") regex: String, config: NotificationObject, factory: (Component, ComponentMatchResult) -> Component = { c, _ -> c }): NotificationType {
            return NotificationType(null, { config }, ComponentRegex(regex), factory)
        }

        fun single(key: String, @Language("RegExp") regex: String, config: () -> NotificationObject): NotificationType {
            return NotificationType(key, config, ComponentRegex(regex))
        }

        fun single(key: String, @Language("RegExp") regex: String, config: NotificationObject, factory: (Component, ComponentMatchResult) -> Component = { c, _ -> c }): NotificationType {
            return NotificationType(key, { config }, ComponentRegex(regex), factory)
        }
    }
}