package tech.thatgravyboat.skycubed.features.info.icons

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.api.displays.Display
import tech.thatgravyboat.skycubed.api.displays.Displays

object LocationIcons : Display {

    private val hubIcon = Displays.sprite(SkyCubed.id("info/icons/locations/hub"), 8, 8)
    private val riftIcon = Displays.sprite(SkyCubed.id("info/icons/locations/rift"), 8, 8)
    private val dwarvesIcon = Displays.sprite(SkyCubed.id("info/icons/locations/dwarves"), 8, 8)
    private val jerryIcon = Displays.sprite(SkyCubed.id("info/icons/locations/jerry"), 8, 8)

    override fun getWidth(): Int = 8
    override fun getHeight(): Int = 8

    override fun render(graphics: GuiGraphics) {
        when (LocationAPI.island) {
            SkyBlockIsland.HUB -> hubIcon
            SkyBlockIsland.THE_RIFT -> riftIcon
            SkyBlockIsland.DWARVEN_MINES -> dwarvesIcon
            SkyBlockIsland.JERRYS_WORKSHOP -> jerryIcon
            else -> Displays.empty(8, 8)
        }.render(graphics)
    }
}