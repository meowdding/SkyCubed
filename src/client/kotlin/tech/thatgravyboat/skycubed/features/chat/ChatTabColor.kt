package tech.thatgravyboat.skycubed.features.chat

import net.minecraft.client.GuiMessageTag
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

object ChatTabColor {

    private val playerMessageRegex = Regex("\\[\\d+] (?:. )?(?:\\[(?:VIP|MVP)\\+*] )?\\w{3,16}: .*")
    private val friendJoinLeaveRegex = Regex("Friend > \\w{3,16} (joined|left)\\.")
    private val partyMessageRegex = Regex("Party > .*")

    private val playerMessageTag = GuiMessageTag(0x888888, null, null, "Hypixel Player")
    private val friendMessageTag = GuiMessageTag(TextColor.GREEN, null, null, "Friend")
    private val partyMessageTag = GuiMessageTag(TextColor.BLUE, null, null, "Party")

    fun getChatColor(message: Component): GuiMessageTag? {
        return when {
            playerMessageRegex.matches(message.stripped) -> playerMessageTag
            friendJoinLeaveRegex.matches(message.stripped) -> friendMessageTag
            partyMessageRegex.matches(message.stripped) -> partyMessageTag
            else -> null
        }
    }
}