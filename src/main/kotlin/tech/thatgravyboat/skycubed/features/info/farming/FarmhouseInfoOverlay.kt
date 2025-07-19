package tech.thatgravyboat.skycubed.features.info.farming

import me.owdding.lib.displays.Displays
import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.area.hub.FarmhouseAPI
import tech.thatgravyboat.skyblockapi.platform.drawSprite
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
        graphics.drawSprite(CommonInfoDisplays.BASE, 0, 0, 34, 34)

        CommonInfoDisplays.locationDisplay.render(graphics, 0, 2, 1f)
        medalsDisplay.render(graphics, 0, 18, 1f)
        CommonInfoDisplays.baseDisplay.render(graphics, 0, 0)
        CommonInfoDisplays.dateDisplay.render(graphics, 34, 2)
        CommonInfoDisplays.currencyDisplay.render(graphics, 34, 18)
    }
}
