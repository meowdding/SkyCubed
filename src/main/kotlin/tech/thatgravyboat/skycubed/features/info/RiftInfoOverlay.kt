package tech.thatgravyboat.skycubed.features.info

import me.owdding.lib.displays.Displays
import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.area.rift.RiftAPI
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.profile.CurrencyAPI
import tech.thatgravyboat.skyblockapi.platform.drawSprite
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skycubed.SkyCubed

object RiftInfoOverlay {

    private val clockIcon = Displays.sprite(SkyCubed.id("info/icons/rift/clock"), 8, 8)
    private val pausedIcon = Displays.sprite(SkyCubed.id("info/icons/rift/paused"), 8, 8)
    private val baseDisplay = Displays.column(
        Displays.empty(34, 5),
        Displays.center(
            34, 12,
            Displays.supplied { if (isTimePaused()) pausedIcon else clockIcon }
        ),
        Displays.empty(34, 3),
        Displays.center(
            34, 10,
            Displays.text(
                ::getRiftTime,
                { if (isTimePaused()) 0xAAAAAAu else 0x55FF55u }
            )
        ),
        Displays.empty(34, 1),
    )

    private val infoDisplay = Displays.background(
        CommonInfoDisplays.RIGHT_LINE,
        Displays.padding(
            0, 3, 2, 2, Displays.row(
                Displays.empty(0, 10),
                Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/rift/timecharm"), 8, 8)),
                Displays.text(
                    { "${RiftAPI.timecharms.first}/${RiftAPI.timecharms.second}" },
                    { 0xFF5555u }
                ),
                Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/rift/engima"), 8, 8)),
                Displays.text(
                    { "${RiftAPI.enigmaSouls.first}/${RiftAPI.enigmaSouls.second}" },
                    { 0xAA00AAu }
                ),
            )
        )
    )

    private val currencyDisplay = Displays.background(
        CommonInfoDisplays.RIGHT_LINE,
        Displays.padding(
            0, 3, 2, 2, Displays.row(
                Displays.empty(0, 10),
                Displays.padding(1, Displays.sprite(SkyCubed.id("info/icons/rift/motes"), 8, 8)),
                Displays.text(
                    { CurrencyAPI.motes.toFormattedString() },
                    { 0xFF55FFu }
                ),
            )
        )
    )

    private val pausedAreas = setOf(
        SkyBlockAreas.WIZARD_TOWER,
        SkyBlockAreas.RIFT_GALLERY,
        SkyBlockAreas.RIFT_GALLERY_ENTRANCE,
        SkyBlockAreas.MIRRORVERSE,
    )

    private fun getRiftTime(): String = RiftAPI.time?.let {
        val minutes = "${it.inWholeMinutes}".padStart(2, '0')
        val seconds = "${it.inWholeSeconds % 60}".padStart(2, '0')
        "$minutes:$seconds"
    } ?: "0s"

    private fun isTimePaused(): Boolean = LocationAPI.area in pausedAreas

    fun render(graphics: GuiGraphics) {
        graphics.drawSprite(CommonInfoDisplays.BASE, 0, 0, 34, 34)

        baseDisplay.render(graphics, 0, 0)
        CommonInfoDisplays.locationDisplay.render(graphics, 0, 2, 1f)
        if (SlayerAPI.type != null) CommonInfoDisplays.slayerDisplay.render(graphics, 0, 18, 1f)
        infoDisplay.render(graphics, 34, 2)
        currencyDisplay.render(graphics, 34, 18)
    }
}
