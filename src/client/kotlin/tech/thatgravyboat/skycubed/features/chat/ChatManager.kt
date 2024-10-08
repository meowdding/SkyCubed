package tech.thatgravyboat.skycubed.features.chat

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent

object ChatManager {

    @Subscription
    fun onChatReceived(event: ChatReceivedEvent) {

    }
}