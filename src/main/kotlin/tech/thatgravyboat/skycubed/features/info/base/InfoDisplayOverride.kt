package tech.thatgravyboat.skycubed.features.info.base

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skycubed.SkyCubed
import kotlin.time.Duration

abstract class InfoDisplayOverride(val island: SkyBlockIsland? = null) {
    abstract fun getIcon(): Display
    abstract fun getText(): String
    abstract fun getTextColor(): UInt

    protected fun icon(id: String) = Displays.sprite(SkyCubed.id("info/icons/$id"), 8, 8)
    protected fun toBeautiful(duration: Duration) = toBeautiful(duration.inWholeMinutes, duration.inWholeSeconds % 60)
    protected fun toBeautiful(first: Number, second: Number) = buildString {
        append(first.toString().padStart(2, '0'))
        append(":")
        append(second.toString().padStart(2, '0'))
    }
}
