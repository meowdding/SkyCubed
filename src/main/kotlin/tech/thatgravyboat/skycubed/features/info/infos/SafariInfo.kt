package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@Module
@RegisterInfoOverlay
object SafariInfo : InfoProvider {
    private var capturedMobs: Int? = null
    private val regex = Regex("Captured Mobs: (?<amount>[\\d,.]+)")

    @Subscription
    @OnlyOnSkyBlock
    fun onScoreboard(event: ScoreboardChangeEvent) {
        val match = regex.anyMatch(event.new, "amount") { (amount) ->
            capturedMobs = amount.toIntValue()
        }
        if (!match) capturedMobs = null
    }

    override val location = InfoLocation.BOTTOM_LEFT

    override val islands = listOf(SkyBlockIsland.SAFARI)

    override fun getDisplay() = DisplayFactory.horizontal {
        display(getIconDisplay(SkyCubed.id("info/icons/pokeball")))
        string(capturedMobs?.let { Text.of(it.toString(), TextColor.YELLOW) } ?: Text.of("N/A", TextColor.RED))
    }
}
