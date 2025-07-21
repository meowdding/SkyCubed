package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Displays
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.anyMatch
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@Module
@RegisterInfoOverlay
object ParkInfo : InfoProvider {
    private var rainTime: Component? = null
    private val rainTimeRegex = ComponentRegex(" Rain: (?<time>.*)")

    @Subscription
    @OnlyOnSkyBlock
    @OnlyWidget(TabWidget.AREA)
    fun onWidgetUpdate(event: TabWidgetChangeEvent) {
        rainTimeRegex.anyMatch(event.newComponents, "time") { (time) ->
            rainTime = time
        }
    }

    override val location = InfoLocation.BOTTOM_LEFT

    override val islands = listOf(SkyBlockIsland.THE_PARK)

    override fun getDisplay() = DisplayFactory.horizontal {
        display(getIconDisplay(SkyCubed.id("info/icons/bucket")))
        Displays.component({ rainTime ?: Text.of("N/A").withColor(TextColor.RED) })
    }
}
