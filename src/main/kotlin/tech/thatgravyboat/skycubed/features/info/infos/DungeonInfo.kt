package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.SecretsActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

// shorter area line
@Module
@RegisterInfoOverlay
object DungeonInfo : InfoProvider {
    override val islands = listOf(SkyBlockIsland.THE_CATACOMBS)
    override val priority: Int = 2
    override val location = InfoLocation.TOP_RIGHT

    private val secretsRegex = " Secrets Found: (?<secrets>\\d+)".toRegex()

    private var roomSecrets: Int = 0
    private var roomMaxSecrets: Int = 0
    private var totalSecrets: Int = 0

    override fun getDisplay() = DisplayFactory.horizontal {
        if (DungeonAPI.bloodOpened) {
            display(getIconDisplay("info/icons/dungeons/blooddoor"))
            textDisplay("Opened", shadow = true) { color = 0xBC1F1D }
        } else {
            fun text(number: Int) = if (number == 0) "❌" to 0xFF5555 else "✔" to 0x55FF55

            display(getIconDisplay("info/icons/dungeons/witherkey"))
            val (witherKeyText, witherKeyColor) = text(DungeonAPI.witherKeys)
            textDisplay(witherKeyText, shadow = true) { color = witherKeyColor }

            display(getIconDisplay("info/icons/dungeons/bloodkey"))
            val (bloodKeyText, bloodKeyColor) = text(DungeonAPI.bloodKeys)
            textDisplay(bloodKeyText, shadow = true) { color = bloodKeyColor }
        }

        display(getIconDisplay("info/icons/dungeons/chest"))
        textDisplay("$roomSecrets/$roomMaxSecrets ($totalSecrets)", shadow = true) { color = 0xA67C39 }
    }

    @Subscription
    fun onActionbarWidget(event: SecretsActionBarWidgetChangeEvent) {
        roomSecrets = event.current
        roomMaxSecrets = event.max
    }

    @Subscription
    @OnlyWidget(TabWidget.DISCOVERIES)
    fun onTabWidget(event: TabWidgetChangeEvent) {
        secretsRegex.anyMatch(event.new, "secrets") { (secrets) ->
            totalSecrets = secrets.toIntOrNull() ?: 0
        }
    }

    @Subscription(event = [ServerChangeEvent::class, ServerDisconnectEvent::class])
    fun onClear() {
        roomSecrets = 0
        roomMaxSecrets = 0
        totalSecrets = 0
    }
}
