package tech.thatgravyboat.skycubed.features.info.icons

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.withPadding
import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skycubed.SkyCubed

object SlayerIcons : Display {

    private val revenant = Displays.sprite(SkyCubed.id("info/icons/slayers/revenant"), 8, 8)
    private val tarantula = Displays.sprite(SkyCubed.id("info/icons/slayers/tarantula"), 8, 8)
    private val sven = Displays.sprite(SkyCubed.id("info/icons/slayers/sven"), 8, 8)
    private val voidgloom = Displays.sprite(SkyCubed.id("info/icons/slayers/voidgloom"), 8, 8)
    private val riftstalker = Displays.sprite(SkyCubed.id("info/icons/slayers/riftstalker"), 8, 8)
    private val inferno = Displays.sprite(SkyCubed.id("info/icons/slayers/inferno"), 8, 8)

    override fun getWidth(): Int = 10
    override fun getHeight(): Int = 10

    override fun render(graphics: GuiGraphics) {
        when (SlayerAPI.type) {
            SlayerType.REVENANT_HORROR -> revenant
            SlayerType.TARANTULA_BROODFATHER -> tarantula
            SlayerType.SVEN_PACKMASTER -> sven
            SlayerType.VOIDGLOOM_SERAPH -> voidgloom
            SlayerType.RIFTSTALKER_BLOODFIEND -> riftstalker
            SlayerType.INFERNO_DEMONLORD -> inferno
            else -> Displays.empty(8, 8)
        }.withPadding(1).render(graphics)
    }
}
