package tech.thatgravyboat.skycubed.features.info

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.area.SlayerAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient

object MainInfoOverlay {

    fun render(graphics: GuiGraphics) {
        val width = McClient.window.guiScaledWidth
        val x = (width - 34) / 2

        graphics.blitSprite(CommonInfoDisplays.BASE, x, 0, 34, 34)

        CommonInfoDisplays.locationDisplay.render(graphics, x, 2, 1f)
        if (SlayerAPI.type != null) CommonInfoDisplays.slayerDisplay.render(graphics, x, 18, 1f)
        CommonInfoDisplays.baseDisplay.render(graphics, x, 0)
        CommonInfoDisplays.dateDisplay.render(graphics, x + 34, 2)
        CommonInfoDisplays.currencyDisplay.render(graphics, x + 34, 18)
    }
}