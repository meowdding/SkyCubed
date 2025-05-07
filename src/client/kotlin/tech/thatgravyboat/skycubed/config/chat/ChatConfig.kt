package tech.thatgravyboat.skycubed.config.chat

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt

object ChatConfig : CategoryKt("chat") {

    override val name: TranslatableValue = Translated("config.skycubed.chat.title")

    var chatColors by boolean("chatColors", true) {
        this.translation = "config.skycubed.chat.chatColors"
    }

    var compactChat by boolean("compactChat", true) {
        this.translation = "config.skycubed.chat.compactChat"
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
            this.translation = "config.skycubed.chat.messagesToClean"
        },
        { it.map { it.pattern }.toTypedArray() },
        { it.mapNotNull { runCatching { Regex(it) }.getOrNull() } },
    )
}