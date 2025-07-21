package tech.thatgravyboat.skycubed.features.info.infos

import me.owdding.lib.builder.DisplayFactory
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.CurrencyAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoLocation
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import tech.thatgravyboat.skycubed.features.info.RegisterInfoOverlay

@RegisterInfoOverlay
object CurrencyInfo : InfoProvider {
    override val location: InfoLocation = InfoLocation.BOTTOM_RIGHT

    override fun getDisplay() = DisplayFactory.horizontal {
        if (SkyBlockIsland.GARDEN.inIsland() or SkyBlockAreas.FARMHOUSE.inArea()) {
            display(getIconDisplay(SkyCubed.id("info/icons/bronze")))
            textDisplay(CurrencyAPI.copper.toFormattedString()) { color = TextColor.RED }
        }

        display(getIconDisplay(SkyCubed.id("info/icons/purse")))
        textDisplay(CurrencyAPI.purse.toFormattedString()) { color = TextColor.GOLD }

        display(getIconDisplay(SkyCubed.id("info/icons/bits")))
        textDisplay(CurrencyAPI.bits.toFormattedString()) { color = TextColor.AQUA }
    }
}
