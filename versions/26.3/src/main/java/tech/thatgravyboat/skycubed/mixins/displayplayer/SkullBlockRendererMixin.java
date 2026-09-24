package tech.thatgravyboat.skycubed.mixins.displayplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayerRenderStateExtension;

@Mixin(SkullBlockRenderer.class)
public class SkullBlockRendererMixin {

    @WrapOperation(method = "submitSkull", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;III)V"))
    private static <S> void onRenderSkull(
        SubmitNodeCollector collector,
        Model<S> model,
        S renderState,
        PoseStack poseStack,
        RenderType renderType,
        int packedLight,
        int packedOverlay,
        int outlineColor,
        Operation<Void> original
    ) {
        if (DisplayEntityPlayerRenderStateExtension.CHANGE_INVISIBLE_RENDERER.get()) {
            collector.submitModel(model, renderState, poseStack, renderType, packedLight, packedOverlay, 0x3FFFFFFF, null, outlineColor);
        } else {
            original.call(collector, model, renderState, poseStack, renderType, packedLight, packedOverlay, outlineColor);
        }
    }

}
