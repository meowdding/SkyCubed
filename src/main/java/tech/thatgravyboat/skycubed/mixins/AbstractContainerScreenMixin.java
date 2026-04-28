//~ gui_graphics
package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tech.thatgravyboat.skycubed.hooks.AbstractContainerScreenAccessor;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements AbstractContainerScreenAccessor {

    //? if > 1.21.11 {
    @Shadow
    protected abstract void extractSlot(GuiGraphicsExtractor par1, Slot par2, int par3, int par4);
    @Override
    public void skycubed$renderSlot(GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY) {
        this.extractSlot(guiGraphics, slot, mouseX, mouseY);
    }
    //?} else {
    /*
    @Shadow protected abstract void renderSlot(net.minecraft.client.gui.GuiGraphics par1, Slot par2, int par3, int par4);
    @Override
    public void skycubed$renderSlot(net.minecraft.client.gui.GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY) {
        this.renderSlot(guiGraphics, slot, mouseX, mouseY);
    }
    *///?}
}
