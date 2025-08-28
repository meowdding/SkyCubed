package tech.thatgravyboat.skycubed.utils

import me.owdding.ktmodules.AutoCollect
import me.owdding.lib.overlays.Overlay
import tech.thatgravyboat.skycubed.SkyCubed

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@AutoCollect("RegisteredOverlays")
annotation class RegisterOverlay

interface SkyCubedOverlay : Overlay {
    override val modId: String get() = SkyCubed.MOD_ID
}
