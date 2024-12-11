package tech.thatgravyboat.skycubed.features.chat

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skycubed.config.chat.ChatConfig

object ChatManager {

    private val compactMessage = mapOf(
        "exp" to Regex("^You earned .* from playing SkyBlock!"),
        "cooldowns" to Regex("^(?:Whoa! Slow down there!|This menu has been throttled! Please slow down\\.\\.\\.)"),
        "item_cooldowns" to Regex("^This ability is on cooldown for \\d+s\\."),
        "friends_list" to Regex("^-*\\n *(?:<<)? Friends \\("),
        "pickaxe_ability" to Regex("^You used your .* Ability!"),
        "gifts" to Regex("Can't place gifts this close to spawn!|You cannot place a gift so close to an NPC!|This gift is for \\w+, sorry!"),
    )

    private var cleanMessages = ChatConfig.messagesToClean.get().map(::Regex)

    init {
        ChatConfig.messagesToClean.addListener { _, new ->
            cleanMessages = new.mapNotNull {
                runCatching { Regex(it) }.getOrNull()
            }
        }
    }

    @Subscription
    fun onChatReceived(event: ChatReceivedEvent) {
        if (ChatConfig.compactChat) {
            for ((id, regex) in compactMessage) {
                if (regex.find(event.text) != null) {
                    event.id = id
                    return
                }
            }
        }
        if (cleanMessages.isNotEmpty()) {
            for (regex in cleanMessages) {
                if (regex.find(event.text) != null) {
                    event.cancel()
                    return
                }
            }
        }
    }


}