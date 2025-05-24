package tech.thatgravyboat.skycubed.features.info

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI

object MainInfoOverlay {

    fun render(graphics: GuiGraphics) {
        graphics.blitSprite(RenderType::guiTextured, CommonInfoDisplays.BASE, 0, 0, 34, 34)

        CommonInfoDisplays.locationDisplay.render(graphics, 0, 2, 1f)
        if (SlayerAPI.type != null) CommonInfoDisplays.slayerDisplay.render(graphics, 0, 18, 1f)
        CommonInfoDisplays.baseDisplay.render(graphics, 0, 0)
        CommonInfoDisplays.dateDisplay.render(graphics, 34, 2)
        CommonInfoDisplays.currencyDisplay.render(graphics, 34, 18)
    }
}
