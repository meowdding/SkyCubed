package tech.thatgravyboat.skycubed.hooks;

import net.minecraft.world.inventory.Slot;

public interface AbstractContainerScreenAccessor {

    //? if > 1.21.11 {
    void skycubed$renderSlot(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY);
    //?} else {
    /*void skycubed$renderSlot(net.minecraft.client.gui.GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY);*/
    //?}
}
