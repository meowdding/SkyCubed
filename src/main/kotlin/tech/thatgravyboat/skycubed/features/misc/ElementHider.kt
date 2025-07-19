package tech.thatgravyboat.skycubed.features.misc

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.info.RenderActionBarWidgetEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudElementEvent
import tech.thatgravyboat.skycubed.config.Config

@Module
object ElementHider {

    @Subscription
    @OnlyOnSkyBlock
    fun onRenderHudElement(event: RenderHudElementEvent) {
        if (event.element in Config.hiddenHudElements) {
            event.cancel()
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onRenderActionBarWidget(event: RenderActionBarWidgetEvent) {
        if (event.widget in Config.hiddenActionBarWidgets) {
            event.cancel()
        }
    }
}
