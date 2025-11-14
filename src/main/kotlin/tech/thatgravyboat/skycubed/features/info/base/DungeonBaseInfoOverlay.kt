package tech.thatgravyboat.skycubed.features.info.base

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
import tech.thatgravyboat.skycubed.features.info.InfoProvider

@Module
object DungeonBaseInfoOverlay : InfoDisplayOverride(SkyBlockIsland.THE_CATACOMBS) {
    private val dungeonFloorIcon = listOf(
        icon("dungeons/entrance"),
        icon("dungeons/bonzo"),
        icon("dungeons/scarf"),
        icon("dungeons/professor"),
        icon("dungeons/thorn"),
        icon("dungeons/livid"),
        icon("dungeons/sadan"),
        icon("dungeons/wither"),
    )
    private val clockIcon = icon("rift/clock")

    private val secretsRegex = " Secrets Found: (?<secrets>\\d+)".toRegex()

    private var roomSecrets: Int = 0
    private var roomMaxSecrets: Int = 0
    private var totalSecrets: Int = 0


    override fun getIcon() = DungeonAPI.dungeonFloor?.floorNumber?.let { dungeonFloorIcon[it] } ?: clockIcon
    override fun getText() = toBeautiful(DungeonAPI.time)
    override fun getTextColor() = 0x55FF55u

    override fun topRight(): InfoProvider = InfoProvider {
        DisplayFactory.horizontal {
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

    @Subscription(ServerChangeEvent::class, ServerDisconnectEvent::class)
    fun onClear() {
        roomSecrets = 0
        roomMaxSecrets = 0
        totalSecrets = 0
    }
}
