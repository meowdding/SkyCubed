package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@RegisterInfoOverlay
// shorter area line
object DungeonInfo : InfoProvider {
    override val islands = listOf(SkyBlockIsland.THE_CATACOMBS)
    override val priority: Int = 2
    override val location = InfoLocation.BOTTOM_RIGHT

    override fun getDisplay() = DisplayFactory.horizontal {
        if (DungeonAPI.bloodOpened) {
            display(getIconDisplay("info/icons/dungeons/blooddoor"))
            textDisplay("Opened", shadow = true) { color = 0xBC1F1D }
        } else {
            fun text(number: Int) = if (number == 0) "❌" to 0xFF5555 else "✔" to 0x55FF55

            display(getIconDisplay("info/icons/dungeons/witherkey"))
            val (witherKeyText, witherKeyColor) = text(DungeonAPI.witherKeys)
            textDisplay(witherKeyText, shadow = true) { color = witherKeyColor }

            display(getIconDisplay("info/icons/dungeons/bloodkey"))
            val (bloodKeyText, bloodKeyColor) = text(DungeonAPI.bloodKeys)
            textDisplay(bloodKeyText, shadow = true) { color = bloodKeyColor }
        }

        display(getIconDisplay("info/icons/dungeons/chest"))
        textDisplay("25", shadow = true) { color = 0xA67C39 }
    }
}
