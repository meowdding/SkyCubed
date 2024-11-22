package tech.thatgravyboat.skycubed.features.info.mining

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import tech.thatgravyboat.skyblockapi.api.area.mining.PowderAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.features.info.CommonInfoDisplays

object CrystalHollowsInfoOverlay {

    private val gemstoneDisplay = Displays.background(
        CommonInfoDisplays.LEFT_LINE,
        Displays.padding(3, 1, 2, 2, Displays.row(
            Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/gemstone"), 8, 8)),
            Displays.text(
                { PowderAPI.gemstone.toFormattedString() },
                { TextColor.LIGHT_PURPLE.toUInt() }
            ),
        ))
    )

    fun render(graphics: GuiGraphics) {
        val width = McClient.window.guiScaledWidth
        val x = (width - 34) / 2

        graphics.blitSprite(RenderType::guiTextured, CommonInfoDisplays.BASE, x, 0, 34, 34)

        CommonInfoDisplays.locationDisplay.render(graphics, x, 2, 1f)
        gemstoneDisplay.render(graphics, x, 18, 1f)
        CommonInfoDisplays.baseDisplay.render(graphics, x, 0)
        CommonInfoDisplays.dateDisplay.render(graphics, x + 34, 2)
        CommonInfoDisplays.currencyDisplay.render(graphics, x + 34, 18)
    }
}