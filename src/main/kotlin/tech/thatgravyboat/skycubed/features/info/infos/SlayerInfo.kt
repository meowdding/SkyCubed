package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toRomanNumeral
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay
import tech.thatgravyboat.skycubed.features.info.icons.SlayerIcons

@RegisterInfoOverlay
object SlayerInfo : InfoProvider {

    override val location = InfoLocation.BOTTOM_LEFT
    override val priority: Int = 1

    override fun shouldDisplay() = SlayerAPI.type != null && super.shouldDisplay()

    override fun getDisplay() = DisplayFactory.horizontal {
        display(SlayerIcons)

        val suffix = when {
            SlayerAPI.max == 0 || SlayerAPI.current == 0 -> "§cInactive!"
            SlayerAPI.current == SlayerAPI.max -> "§aComplete!"
            SlayerAPI.text != null -> SlayerAPI.text
            else -> "§e${SlayerAPI.current}§7/§c${SlayerAPI.max}"
        }
        string("§a${SlayerAPI.level.toRomanNumeral(true)} $suffix")
    }
}
