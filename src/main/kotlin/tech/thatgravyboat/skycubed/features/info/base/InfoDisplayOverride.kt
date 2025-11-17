package tech.thatgravyboat.skycubed.features.info.base

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.features.info.InfoProvider
import kotlin.time.Duration

abstract class InfoDisplayOverride(val island: SkyBlockIsland? = null) {
    abstract fun getIcon(): Display
    abstract fun getText(): String
    abstract fun getTextColor(): UInt

    open fun topLeft(): InfoProvider? = null
    open fun topRight(): InfoProvider? = null
    open fun bottomLeft(): InfoProvider? = null
    open fun bottomRight(): InfoProvider? = null

    protected fun icon(id: String) = Displays.sprite(SkyCubed.id("info/icons/$id"), 8, 8)
    protected fun formatTime(duration: Duration) = formatTime(duration.inWholeMinutes, duration.inWholeSeconds % 60)
    protected fun formatTime(first: Number, second: Number) = buildString {
        append(first.toString().padStart(2, '0'))
        append(":")
        append(second.toString().padStart(2, '0'))
    }
}
