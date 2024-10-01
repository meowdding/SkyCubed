package tech.thatgravyboat.skycubed.features.info

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.area.mining.PowderAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.api.displays.Displays

object GlaciteInfoOverlay {

    private val glaciteDisplay = Displays.background(
        CommonInfoDisplays.LEFT_LINE,
        Displays.padding(3, 1, 2, 2, Displays.row(
            Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/glacite"), 8, 8)),
            Displays.text(
                { PowderAPI.glacite.toFormattedString() },
                { 0x55FFFFu }
            ),
        ))
    )

    fun render(graphics: GuiGraphics) {
        val width = McClient.window.guiScaledWidth
        val x = (width - 34) / 2

        graphics.blitSprite(CommonInfoDisplays.BASE, x, 0, 34, 34)

        CommonInfoDisplays.locationDisplay.render(graphics, x, 2, 1f)
        glaciteDisplay.render(graphics, x, 18, 1f)
        CommonInfoDisplays.baseDisplay.render(graphics, x, 0)
        CommonInfoDisplays.dateDisplay.render(graphics, x + 34, 2)
        CommonInfoDisplays.currencyDisplay.render(graphics, x + 34, 18)
    }
}