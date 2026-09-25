package tech.thatgravyboat.skycubed.hooks;

import net.minecraft.world.inventory.Slot;

public interface AbstractContainerScreenAccessor {

    void skycubed$renderSlot(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY);
}
