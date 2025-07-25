package tech.thatgravyboat.skycubed.utils

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.AbstractClientPlayer


actual object Utils {

    actual fun drawRpgPlayer(
        graphics: GuiGraphics,
        entity: AbstractClientPlayer,
        width: Int, height: Int, scale: Float,
    ) {
    }
}
