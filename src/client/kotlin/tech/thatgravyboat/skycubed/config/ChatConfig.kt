package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable

@Category("chat")
object ChatConfig {

    @ConfigEntry(id = "chatColors", translation = "config.skycubed.chat.chatColors")
    @Comment("", translation = "config.skycubed.chat.chatColors.desc")
    var chatColors = true

    @ConfigEntry(id = "compactChat", translation = "config.skycubed.chat.compactChat")
    @Comment("", translation = "config.skycubed.chat.compactChat.desc")
    var compactChat = true

    @ConfigEntry(id = "messagesToClean", translation = "config.skycubed.chat.messagesToClean")
    @Comment("", translation = "config.skycubed.chat.messagesToClean.desc")
    val messagesToClean: Observable<Array<String>> = Observable.of(
        arrayOf(
            "^Profile ID:",
            "^You are playing on profile:",
            "^\\[WATCHDOG ANNOUNCEMENT]",
            "^Watchdog has banned",
            "^Staff have banned an additional",
            "^Blacklisted modifications are a bannable offense!",
        )
    )


}