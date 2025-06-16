package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skycubed.features.overlays.VanillaBossbarOverlay;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V"
            ),
            cancellable = true
    )
    private void onRenderFull(GuiGraphics guiGraphics, CallbackInfo ci, @Local LerpingBossEvent event) {
        if (VanillaBossbarOverlay.INSTANCE.onRenderFull(event)) {
            ci.cancel();
        }
    }

    @WrapWithCondition(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V"
            )
    )
    private boolean onRenderBar(BossHealthOverlay instance, GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent) {
        return VanillaBossbarOverlay.INSTANCE.onRenderTitle(bossEvent);
    }
}
