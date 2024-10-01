package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement;
import tech.thatgravyboat.skycubed.config.Config;

@Mixin(EffectRenderingInventoryScreen.class)
public class EffectRenderingInventoryScreenMixin {

    @Unique
    private HudElement[] lastElements = new HudElement[0];

    @Unique
    private boolean shouldRender = true;

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/EffectRenderingInventoryScreen;renderEffects(Lnet/minecraft/client/gui/GuiGraphics;II)V"
            )
    )
    private void renderEffects(EffectRenderingInventoryScreen<?> instance, GuiGraphics guiGraphics, int i, int j, Operation<Void> original) {
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

        if (shouldRender) {
            original.call(instance, guiGraphics, i, j);
        }
    }
}
