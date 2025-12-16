package tech.thatgravyboat.skycubed.hooks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;

public interface AbstractContainerScreenAccessor {

    void skycubed$renderSlot(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY);
}
