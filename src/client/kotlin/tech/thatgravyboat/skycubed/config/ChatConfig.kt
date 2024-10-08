package tech.thatgravyboat.skycubed.config

import com.teamresourceful.resourcefulconfig.api.annotations.Category
import com.teamresourceful.resourcefulconfig.api.annotations.Comment
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry

@Category("chat")
object ChatConfig {

    @ConfigEntry(id = "chatColors", translation = "config.skycubed.chat.chatColors")
    @Comment("", translation = "config.skycubed.chat.chatColors.desc")
    var chatColors = true

    @ConfigEntry(id = "compactChat", translation = "config.skycubed.chat.compactChat")
    @Comment("", translation = "config.skycubed.chat.compactChat.desc")
    var compactChat = true
}