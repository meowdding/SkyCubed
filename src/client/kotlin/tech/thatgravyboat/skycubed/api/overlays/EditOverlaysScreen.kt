package tech.thatgravyboat.skycubed.api.overlays

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.CommonText

class EditOverlaysScreen : Screen(CommonText.EMPTY) {

    override fun renderBackground(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        guiGraphics.fill(0, 0, width, height, 0x40000000)
        guiGraphics.drawCenteredString(font, "Edit Overlays", width / 2, height / 2, 0xFFFFFF)
    }

    companion object {
        fun inScreen() = McClient.self.screen is EditOverlaysScreen
    }
}