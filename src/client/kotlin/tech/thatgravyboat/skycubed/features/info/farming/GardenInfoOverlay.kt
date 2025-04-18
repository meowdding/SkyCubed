package tech.thatgravyboat.skycubed.features.info.farming

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import tech.thatgravyboat.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI
import tech.thatgravyboat.skyblockapi.api.profile.CurrencyAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.CommonInfoDisplays
import tech.thatgravyboat.skycubed.features.info.CommonInfoDisplays.RIGHT_LINE

object GardenInfoOverlay {

    private val currencyDisplay = Displays.background(
        RIGHT_LINE,
        Displays.padding(0, 3, 2, 2, Displays.row(
            Displays.row(
                Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/bronze"), 8, 8)),
                Displays.text(
                    { CurrencyAPI.copper.toFormattedString() },
                    { TextColor.RED.toUInt() }
                ),
            ),
            Displays.row(
                Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/purse"), 8, 8)),
                Displays.text(
                    { CurrencyAPI.purse.toFormattedString() },
                    { TextColor.GOLD.toUInt() }
                ),
            ),
            Displays.row(
                Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/bits"), 8, 8)),
                Displays.text(
                    { CurrencyAPI.bits.toFormattedString() },
                    { TextColor.AQUA.toUInt() }
                ),
            ),
        ))
    )

    fun render(graphics: GuiGraphics) {
        val width = McClient.window.guiScaledWidth
        val x = (width - 34) / 2

        graphics.blitSprite(RenderType::guiTextured, CommonInfoDisplays.BASE, x, 0, 34, 34)

        CommonInfoDisplays.locationDisplay.render(graphics, x, 2, 1f)
        if (SlayerAPI.type != null) CommonInfoDisplays.slayerDisplay.render(graphics, x, 18, 1f)
        CommonInfoDisplays.baseDisplay.render(graphics, x, 0)
        CommonInfoDisplays.dateDisplay.render(graphics, x + 34, 2)
        currencyDisplay.render(graphics, x + 34, 18)
    }
}