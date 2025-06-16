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
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties;
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig;

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
    private void updateTitle(GuiGraphics guiGraphics, CallbackInfo ci, @Local LerpingBossEvent event) {
        if (OverlaysConfig.INSTANCE.getVanillaBossbar().getRemoveWhenObjective() && TextProperties.INSTANCE.getStripped(event.getName()).contains("Objective: ")) {
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
    private boolean renderBossBar(BossHealthOverlay instance, GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent) {
        if (OverlaysConfig.INSTANCE.getVanillaBossbar().getRemoveBarWhenFull()) {
            return bossEvent.getProgress() < 1.0F;
        }
        return true;
    }
}
