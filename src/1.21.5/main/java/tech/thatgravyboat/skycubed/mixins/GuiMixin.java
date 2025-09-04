package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import me.owdding.lib.overlays.Position;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.area.mining.GlaciteAPI;
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions;
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig;
import tech.thatgravyboat.skycubed.features.overlays.vanilla.MovableHotbar;

@Mixin(Gui.class)
public class GuiMixin {

    @ModifyExpressionValue(method = "renderCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getTicksFrozen()I"))
    private int modifyCameraOverlays(int ticks) {
        var cold = GlaciteAPI.INSTANCE.getCold();
        var coldStart = OverlaysConfig.INSTANCE.getColdOverlay();
        if (cold > coldStart && coldStart != 0) {
            return 1;
        }
        return ticks;
    }

    @WrapOperation(
            method = "renderCameraOverlays",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;renderTextureOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;F)V",
                    ordinal = 1
            )
    )
    private void wrapRenderCameraOverlays(Gui instance, GuiGraphics guiGraphics, ResourceLocation resourceLocation, float f, Operation<Void> original) {
        var cold = GlaciteAPI.INSTANCE.getCold();
        var coldStart = OverlaysConfig.INSTANCE.getColdOverlay();
        if (cold > coldStart && coldStart != 0) {
            var percent = (cold - coldStart) / (100f - coldStart);
            original.call(instance, guiGraphics, resourceLocation, percent);
        } else {
            original.call(instance, guiGraphics, resourceLocation, f);
        }
    }

    @WrapMethod(method = "renderItemHotbar")
    private void wrapRenderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (!MovableHotbar.INSTANCE.getEnabled()) {
            original.call(guiGraphics, deltaTracker);
            return;
        }

        PoseStack stack = guiGraphics.pose();
        stack.pushPose();

        Position position = OverlayPositions.INSTANCE.getHotbar();
        float scale = position.getScale();

        stack.translate(position.component1(), position.component2(), 0);
        stack.scale(scale, scale, 1f);

        // Reset the Hotbar to top left
        stack.translate(-((float) guiGraphics.guiWidth() / 2 - 91), -(guiGraphics.guiHeight() - 22), 0);

        original.call(guiGraphics, deltaTracker);

        stack.popPose();
    }

}
