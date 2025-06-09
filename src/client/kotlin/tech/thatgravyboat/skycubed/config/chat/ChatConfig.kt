package tech.thatgravyboat.skycubed.config.chat

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object ChatConfig : CategoryKt("chat") {

    override val name: TranslatableValue = Translated("skycubed.config.chat")

    var modifyHypixelCommands by boolean(true) {
        this.translation = "skycubed.config.chat.modify_commands"
    }

    var chatColors by boolean(true) {
        this.translation = "skycubed.config.chat.chat_colors"
    }

    var compactChat by boolean(true) {
        this.translation = "skycubed.config.chat.compact_chat"
    }

    val messagesToClean by transform(
        strings(
            "^Profile ID:",
            "^You are playing on profile:",
            "^\\[WATCHDOG ANNOUNCEMENT]",
            "^Watchdog has banned",
            "^Staff have banned an additional",
            "^Blacklisted modifications are a bannable offense!",
            "^Couldn't warp you! Try again later.",
            "^ *A FIRE SALE.*to grab yours!$",
        ) {
            this.translation = "skycubed.config.chat.messages_to_clean"
        },
        { it.map { it.pattern }.toTypedArray() },
        { it.mapNotNull { runCatching { Regex(it) }.getOrNull() } },
    )
}
