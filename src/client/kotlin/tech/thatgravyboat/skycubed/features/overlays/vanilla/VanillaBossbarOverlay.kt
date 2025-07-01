package tech.thatgravyboat.skycubed.features.overlays.vanilla

import net.minecraft.client.gui.components.LerpingBossEvent
import net.minecraft.world.BossEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.config.overlays.BossbarOverlayConfig

object VanillaBossbarOverlay {
    // Return true to disable
    fun onRenderFull(event: LerpingBossEvent): Boolean {
        if (BossbarOverlayConfig.removeBarWhenObjective) {
            if (event.name.stripped.contains("Objective: ")) return true
        }
        return false
    }

    // return true to keep enabled
    fun onRenderTitle(event: BossEvent): Boolean {
        if (BossbarOverlayConfig.removeWhenFull) {
            return event.progress < 1.0F
        }
        return true
    }
}

interface BossEventExtension {
    var `skycubed$disabled`: Boolean
    var `skycubed$barDisabled`: Boolean
}

val LerpingBossEvent.disabled : Boolean
    get() = (this as BossEventExtension).`skycubed$disabled`

val LerpingBossEvent.barDisabled : Boolean
    get() = (this as BossEventExtension).`skycubed$barDisabled`
