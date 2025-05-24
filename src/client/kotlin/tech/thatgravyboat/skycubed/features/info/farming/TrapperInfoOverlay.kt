package tech.thatgravyboat.skycubed.features.info.farming

import me.owdding.lib.displays.Displays
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import tech.thatgravyboat.skyblockapi.api.area.farming.TrapperAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.CommonInfoDisplays

object TrapperInfoOverlay {

    private val peltsDisplay = Displays.background(
        CommonInfoDisplays.LEFT_LINE,
        Displays.padding(3, 1, 2, 2, Displays.row(
            Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/pelts"), 8, 8)),
            Displays.text(
                { TrapperAPI.pelts.toFormattedString() },
                { TextColor.DARK_PURPLE.toUInt() }
            ),
        ))
    )

    fun render(graphics: GuiGraphics) {
        graphics.blitSprite(RenderType::guiTextured, CommonInfoDisplays.BASE, 0, 0, 34, 34)

        CommonInfoDisplays.locationDisplay.render(graphics, 0, 2, 1f)
        peltsDisplay.render(graphics, 0, 18, 1f)
        CommonInfoDisplays.baseDisplay.render(graphics, 0, 0)
        CommonInfoDisplays.dateDisplay.render(graphics, 34, 2)
        CommonInfoDisplays.currencyDisplay.render(graphics, 34, 18)
    }
}
