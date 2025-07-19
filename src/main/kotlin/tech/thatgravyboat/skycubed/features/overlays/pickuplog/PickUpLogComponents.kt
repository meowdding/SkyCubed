package tech.thatgravyboat.skycubed.features.overlays.pickuplog

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

enum class PickUpLogComponents(val display: (PickUpLogItem) -> Display) {
    ITEM_STACK({ Displays.item(it.stack, 10, 10) }),
    DIFFERENCE({
        if (it.difference < 0) Displays.text(Text.of(it.difference.toFormattedString()).withColor(TextColor.RED))
        else Displays.text(Text.of("+${it.difference.toFormattedString()}").withColor(TextColor.GREEN))
    }),
    NAME({ Displays.text(it.stack.hoverName) }),
    ;

    private val formattedName = name.split("_").joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }

    override fun toString() = formattedName

}
