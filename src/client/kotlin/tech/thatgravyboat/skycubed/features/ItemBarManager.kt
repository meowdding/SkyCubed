package tech.thatgravyboat.skycubed.features

import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.LoreDataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderItemBarEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

object ItemBarManager {

    // TODO pickobolus and armadillo egg

    @Subscription
    fun onRenderItemBar(event: RenderItemBarEvent) {
        event.item.getData(DataTypes.FUEL)?.let {
            event.color = TextColor.DARK_GREEN
            event.percent = it.first.toFloat() / it.second.toFloat()
        }
        event.item.getData(LoreDataTypes.SNOWBALLS)?.let {
            event.color = TextColor.WHITE
            event.percent = it.first.toFloat() / it.second.toFloat()
        }
    }

    //
}