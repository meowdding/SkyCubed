package tech.thatgravyboat.skycubed.mixins;

import earth.terrarium.olympus.client.ui.Overlay;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(value = Overlay.class, remap = false)
public interface OverlayAccessor {

    @Accessor(value = "background")
    Screen skycubed$getBackgroundScreen();
}
