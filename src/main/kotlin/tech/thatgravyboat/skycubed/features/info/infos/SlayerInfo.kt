package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toRomanNumeral
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.icons.SlayerIcons

object SlayerInfo : InfoProvider {

    override val location = InfoLocation.BOTTOM_LEFT

    override fun getDisplay() = DisplayFactory.horizontal {
        display(SlayerIcons)
        spacer(2, 10)

        val suffix = when {
            SlayerAPI.max == 0 || SlayerAPI.current == 0 -> "§cInactive!"
            SlayerAPI.current == SlayerAPI.max -> "§aComplete!"
            SlayerAPI.text != null -> SlayerAPI.text
            else -> "§e${SlayerAPI.current}§7/§c${SlayerAPI.max}"
        }
        string("§a${SlayerAPI.level.toRomanNumeral(true)} $suffix")
    }
}
