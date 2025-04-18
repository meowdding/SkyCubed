package tech.thatgravyboat.skycubed.features.info.farming

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import tech.thatgravyboat.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.area.hub.FarmhouseAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.CommonInfoDisplays

object FarmhouseInfoOverlay {

    private val medalsDisplay = Displays.background(
        CommonInfoDisplays.LEFT_LINE,
        Displays.padding(3, 1, 2, 2, Displays.row(
            Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/gold"), 8, 8)),
            Displays.text(
                { FarmhouseAPI.goldMedals.toFormattedString() },
                { TextColor.GOLD.toUInt() }
            ),
            Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/silver"), 8, 8)),
            Displays.text(
                { FarmhouseAPI.silverMedals.toFormattedString() },
                { TextColor.WHITE.toUInt() }
            ),
            Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/bronze"), 8, 8)),
            Displays.text(
                { FarmhouseAPI.bronzeMedals.toFormattedString() },
                { TextColor.RED.toUInt() }
            ),
        ))
    )

    fun render(graphics: GuiGraphics) {
        val width = McClient.window.guiScaledWidth
        val x = (width - 34) / 2

        graphics.blitSprite(RenderType::guiTextured, CommonInfoDisplays.BASE, x, 0, 34, 34)

        CommonInfoDisplays.locationDisplay.render(graphics, x, 2, 1f)
        medalsDisplay.render(graphics, x, 18, 1f)
        CommonInfoDisplays.baseDisplay.render(graphics, x, 0)
        CommonInfoDisplays.dateDisplay.render(graphics, x + 34, 2)
        CommonInfoDisplays.currencyDisplay.render(graphics, x + 34, 18)
    }
}