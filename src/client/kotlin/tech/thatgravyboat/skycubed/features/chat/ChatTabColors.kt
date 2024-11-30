package tech.thatgravyboat.skycubed.features.chat

import net.minecraft.client.GuiMessageTag
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.config.ChatConfig

object ChatTabColors {

    private val playerMessageRegex = Regex("\\[\\d+] (?:. )?(?:\\[(?:VIP|MVP)\\+*] )?\\w{3,16}: .*")
    private val friendJoinLeaveRegex = Regex("Friend > \\w{3,16} (joined|left)\\.")
    private val partyMessageRegex = Regex("Party > .*")
    private val guildMessageRegex = Regex("Guild > .*")
    private val privateMessageRegex = Regex("(?:To|From) .*: .*")

    private val playerMessageTag = GuiMessageTag(TextColor.DARK_GRAY, null, null, "Hypixel Player")
    private val friendMessageTag = GuiMessageTag(TextColor.GREEN, null, null, "Friend")
    private val partyMessageTag = GuiMessageTag(TextColor.BLUE, null, null, "Party")
    private val guildMessageTag = GuiMessageTag(TextColor.DARK_GREEN, null, null, "Guild")
    private val privateMessageTag = GuiMessageTag(TextColor.LIGHT_PURPLE, null, null, "Private Message")

    fun getChatColor(message: Component): GuiMessageTag? {
        if (!ChatConfig.chatColors) return null
        val text = message.stripped
        return when {
            playerMessageRegex.match(text) -> playerMessageTag
            friendJoinLeaveRegex.match(text) -> friendMessageTag
            partyMessageRegex.match(text) -> partyMessageTag
            guildMessageRegex.match(text) -> guildMessageTag
            privateMessageRegex.match(text) -> privateMessageTag
            else -> null
        }
    }
}