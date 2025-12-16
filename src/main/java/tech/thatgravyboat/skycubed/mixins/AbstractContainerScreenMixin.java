package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tech.thatgravyboat.skycubed.hooks.AbstractContainerScreenAccessor;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements AbstractContainerScreenAccessor {

    //? if > 1.21.10 {

    @Shadow protected abstract void renderSlot(GuiGraphics par1, Slot par2, int par3, int par4);
    @Override
    public void skycubed$renderSlot(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY) {
        this.renderSlot(guiGraphics, slot, mouseX, mouseY);
    }

    //?} else {
    /*
    @Shadow public abstract void renderSlot(GuiGraphics guiGraphics, Slot slot);
    @Override
    public void skycubed$renderSlot(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY) {
        this.renderSlot(guiGraphics, slot);
    }
    */
    //?}
}
