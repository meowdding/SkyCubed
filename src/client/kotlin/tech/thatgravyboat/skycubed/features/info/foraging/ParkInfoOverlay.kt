package tech.thatgravyboat.skycubed.features.info.foraging

import me.owdding.ktmodules.Module
import me.owdding.lib.displays.Displays
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.anyMatch
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.CommonInfoDisplays

@Module
object ParkInfoOverlay {
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

    private val rainTimeDisplay by lazy {
        Displays.background(
            CommonInfoDisplays.LEFT_LINE,
            Displays.padding(
                3, 1, 2, 2, Displays.row(
                    Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/bucket"), 8, 8)),
                    Displays.component({ rainTime ?: Text.of("N/A").withColor(TextColor.RED) })
                )
            )
        )
    }

    fun render(graphics: GuiGraphics) {
        val width = McClient.window.guiScaledWidth
        val x = (width - 34) / 2

        graphics.blitSprite(RenderType::guiTextured, CommonInfoDisplays.BASE, x, 0, 34, 34)

        CommonInfoDisplays.locationDisplay.render(graphics, x, 2, 1f)
        rainTimeDisplay.render(graphics, x, 18, 1f)
        CommonInfoDisplays.baseDisplay.render(graphics, x, 0)
        CommonInfoDisplays.dateDisplay.render(graphics, x + 34, 2)
        CommonInfoDisplays.currencyDisplay.render(graphics, x + 34, 18)
    }
}
