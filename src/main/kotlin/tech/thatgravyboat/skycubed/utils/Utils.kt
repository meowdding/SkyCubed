package tech.thatgravyboat.skycubed.utils

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.AbstractClientPlayer
import net.msrandom.stub.Stub

@Stub
expect object Utils {

    fun drawRpgPlayer(
        graphics: GuiGraphics,
        entity: AbstractClientPlayer,
        width: Int, height: Int, scale: Float,
    )
}
