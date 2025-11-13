package tech.thatgravyboat.skycubed.features.info.base

import tech.thatgravyboat.skyblockapi.api.datetime.DateTimeAPI

object DefaultBaseInfoOverlay : InfoDisplayOverride() {
    private val sunIcon = icon("sun")
    private val moonIcon = icon("moon")

    override fun getIcon() = if (DateTimeAPI.isDay) sunIcon else moonIcon
    override fun getText() = toBeautiful(DateTimeAPI.hour, DateTimeAPI.minute)
    override fun getTextColor() = if (DateTimeAPI.isDay) 0xFFFF55u else 0xAAAAAAu
}
