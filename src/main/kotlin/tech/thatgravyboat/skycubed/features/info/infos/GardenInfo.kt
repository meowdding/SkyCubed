package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetUpdateEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.garden.PlotAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.fromNow
import tech.thatgravyboat.skyblockapi.utils.extentions.parseDuration
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.extentions.until
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay
import tech.thatgravyboat.skycubed.utils.formatReadableTime
import kotlin.time.DurationUnit
import kotlin.time.Instant

@Module
@RegisterInfoOverlay
object GardenInfo : InfoProvider {
    override val location: InfoLocation = InfoLocation.BOTTOM_LEFT

    override fun getDisplay() = DisplayFactory.horizontal {
        display(getIconDisplay(SkyCubed.id("info/icons/pest")))
        textDisplay("${PlotAPI.currentPestAmount}", shadow = true) { color = TextColor.DARK_GREEN }

        display(getIconDisplay(SkyCubed.id("info/icons/visitor")))
        textDisplay("$visitorAmount", shadow = true) { color = TextColor.AQUA }
        visitorTime?.let {
            textDisplay(" (${it.until().formatReadableTime(DurationUnit.MINUTES, 2)})", shadow = true) { color = TextColor.GREEN }
        } ?: run {
            textDisplay(" (Full)", shadow = true) { color = TextColor.RED }
        }
    }

    override fun shouldDisplay() = SkyBlockIsland.GARDEN.inIsland() && PlotAPI.currentPestAmount > 0

    private val visitorTimeRegex = " Next Visitor: (?:Queue Full!|(?<time>[\\w\\s]+))".toRegex()
    private var visitorAmount = 0
    private var visitorTime: Instant? = null

    @Subscription
    @OnlyWidget(TabWidget.VISITORS)
    fun onTabWidget(event: TabWidgetUpdateEvent) {
        TabWidget.VISITORS.regex.anyMatch(event.new, "amount") { (amount) ->
            visitorAmount = amount.toIntValue()
        }
        visitorTimeRegex.anyMatch(event.new.reversed()) { destr ->
            visitorTime = destr["time"]?.parseDuration()?.fromNow()
        }
    }
}
