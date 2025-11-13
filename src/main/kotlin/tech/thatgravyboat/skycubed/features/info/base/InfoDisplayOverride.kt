package tech.thatgravyboat.skycubed.features.info.base

import me.owdding.lib.displays.Display
import me.owdding.lib.displays.Displays
import tech.thatgravyboat.skycubed.SkyCubed
import kotlin.time.Duration

interface InfoDisplayOverride {
    fun getIcon(): Display
    fun getText(): String
    fun getTextColor(): UInt

    fun icon(id: String) = Displays.sprite(SkyCubed.id("info/icons/$id"), 8, 8)
    fun toBeautiful(duration: Duration) = toBeautiful(duration.inWholeMinutes, duration.inWholeSeconds % 60)
    fun toBeautiful(first: Number, second: Number) = buildString {
        append(first.toString().padStart(2, '0'))
        append(":")
        append(second.toString().padStart(2, '0'))
    }
}
