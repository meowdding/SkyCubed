package tech.thatgravyboat.skycubed.features.info.icons

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.withPadding
import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skycubed.SkyCubed

object LocationIcons : Display {

    private val hub = Displays.sprite(SkyCubed.id("info/icons/locations/hub"), 8, 8)
    private val rift = Displays.sprite(SkyCubed.id("info/icons/locations/rift"), 8, 8)
    private val dwarves = Displays.sprite(SkyCubed.id("info/icons/locations/dwarves"), 8, 8)
    private val jerry = Displays.sprite(SkyCubed.id("info/icons/locations/jerry"), 8, 8)
    private val garden = Displays.sprite(SkyCubed.id("info/icons/locations/garden"), 8, 8)
    private val kuudra = Displays.sprite(SkyCubed.id("info/icons/locations/kuudra"), 8, 8)
    private val mines = Displays.sprite(SkyCubed.id("info/icons/locations/mines"), 8, 8)

    override fun getWidth(): Int = 10
    override fun getHeight(): Int = 8

    override fun render(graphics: GuiGraphics) {
        when (LocationAPI.island) {
            SkyBlockIsland.HUB -> hub
            SkyBlockIsland.THE_RIFT -> rift
            SkyBlockIsland.DWARVEN_MINES -> dwarves
            SkyBlockIsland.GOLD_MINES, SkyBlockIsland.DEEP_CAVERNS -> mines
            SkyBlockIsland.JERRYS_WORKSHOP -> jerry
            SkyBlockIsland.GARDEN -> garden
            SkyBlockIsland.KUUDRA -> kuudra
            else -> hub
        }.withPadding(left = 1, right = 1).render(graphics)
    }
}
