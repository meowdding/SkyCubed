package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement;
import tech.thatgravyboat.skycubed.config.Config;

@Mixin(EffectsInInventory.class)
public class EffectsInInventoryMixin {

    @Unique
    private HudElement[] lastElements = new HudElement[0];

    @Unique
    private boolean shouldRender = true;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void renderEffects(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (Config.INSTANCE.getHiddenHudElements() != lastElements) {
            lastElements = Config.INSTANCE.getHiddenHudElements();
            shouldRender = true;
            for (HudElement element : Config.INSTANCE.getHiddenHudElements()) {
                if (element == HudElement.EFFECTS) {
                    shouldRender = false;
                    break;
                }
            }
        }
        if (!shouldRender) {
            ci.cancel();
        }
    }
}
