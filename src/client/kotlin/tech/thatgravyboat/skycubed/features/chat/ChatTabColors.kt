package tech.thatgravyboat.skycubed.features.chat

import net.minecraft.client.GuiMessageTag
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.config.ChatConfig

object ChatTabColors {

    private val playerMessageRegex = Regex("\\[\\d+] (?:. )?(?:\\[(?:VIP|MVP)\\+*] )?\\w{3,16}: .*")
    private val friendJoinLeaveRegex = Regex("Friend > \\w{3,16} (joined|left)\\.")
    private val partyMessageRegex = Regex("Party > .*")
    private val guildMessageRegex = Regex("Guild > .*")

    private val playerMessageTag = GuiMessageTag(0x888888, null, null, "Hypixel Player")
    private val friendMessageTag = GuiMessageTag(TextColor.GREEN, null, null, "Friend")
    private val partyMessageTag = GuiMessageTag(TextColor.BLUE, null, null, "Party")
    private val guildMessageTag = GuiMessageTag(TextColor.DARK_GREEN, null, null, "Guild")

    fun getChatColor(message: Component): GuiMessageTag? {
        if (!ChatConfig.chatColors) return null
        return when {
            playerMessageRegex.matches(message.stripped) -> playerMessageTag
            friendJoinLeaveRegex.matches(message.stripped) -> friendMessageTag
            partyMessageRegex.matches(message.stripped) -> partyMessageTag
            guildMessageRegex.matches(message.stripped) -> guildMessageTag
            else -> null
        }
    }
}