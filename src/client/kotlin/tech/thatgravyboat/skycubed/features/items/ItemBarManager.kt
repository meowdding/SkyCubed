package tech.thatgravyboat.skycubed.features.items

import me.owdding.ktmodules.Module
import net.minecraft.util.Mth
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderItemBarEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.config.items.ItemsConfig

@Module
object ItemBarManager {

    // TODO armadillo egg

    @Subscription
    fun onRenderItemBar(event: RenderItemBarEvent) {
        if (!ItemsConfig.itembars) return
        event.item.getData(DataTypes.FUEL)?.let {
            event.color = TextColor.DARK_GREEN
            event.percent = it.first.toFloat() / it.second.toFloat()
        }
        event.item.getData(DataTypes.SNOWBALLS)?.let {
            event.color = TextColor.WHITE
            event.percent = it.first.toFloat() / it.second.toFloat()
        }
        event.item.getData(DataTypes.PICKONIMBUS_DURABILITY)?.let {
            event.percent = it / 2000f
            event.color = Mth.hsvToRgb(event.percent / 3.0F, 1.0F, 1.0F)
        }
    }

    //
}
