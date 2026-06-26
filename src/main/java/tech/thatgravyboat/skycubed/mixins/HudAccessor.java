package tech.thatgravyboat.skycubed.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? >= 26.2 {
@Mixin(net.minecraft.client.gui.Hud.class)
//?} else
//@Mixin(net.minecraft.client.gui.Gui.class)
public interface HudAccessor {

    @Accessor("toolHighlightTimer")
    void setToolHighlightTimer(int ticks);
}
