package tech.thatgravyboat.skycubed.mixins.displayplayer;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayerRenderStateExtension;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    @WrapMethod(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V"
    )
    private void onRender(
        PoseStack poseStack,
        SubmitNodeCollector collector,
        int packedLight,
        HumanoidRenderState renderState,
        float yRot,
        float xRot,
        Operation<Void> original
    ) {
        if (renderState instanceof DisplayEntityPlayerRenderStateExtension extension && extension.skycubed$isDisplayEntityPlayer() && renderState.isInvisible) {
            DisplayEntityPlayerRenderStateExtension.CHANGE_INVISIBLE_RENDERER.set(Boolean.TRUE);
            original.call(poseStack, collector, packedLight, renderState, yRot, xRot);
            DisplayEntityPlayerRenderStateExtension.CHANGE_INVISIBLE_RENDERER.set(Boolean.FALSE);
        } else {
            original.call(poseStack, collector, packedLight, renderState, yRot, xRot);
        }
    }
}
