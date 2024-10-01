package tech.thatgravyboat.skycubed.features

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.RenderActionBarWidgetEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudElementEvent
import tech.thatgravyboat.skycubed.config.Config

object ElementHider {

    @Subscription
    fun onRenderHudElement(event: RenderHudElementEvent) {
        if (event.element in Config.hiddenHudElements) {
            event.cancel()
        }
    }

    @Subscription
    fun onRenderActionBarWidget(event: RenderActionBarWidgetEvent) {
        if (event.widget in Config.hiddenActionBarWidgets) {
            event.cancel()
        }
    }
}