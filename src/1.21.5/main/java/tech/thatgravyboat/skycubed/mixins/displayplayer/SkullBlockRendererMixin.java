package tech.thatgravyboat.skycubed.mixins.displayplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayerRenderStateExtension;

@Mixin(SkullBlockRenderer.class)
public class SkullBlockRendererMixin {

    @WrapOperation(method = "renderSkull", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/SkullModelBase;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private static void onRenderSkull(SkullModelBase model, PoseStack stack, VertexConsumer vertex, int light, int overlay, Operation<Void> original) {
        if (DisplayEntityPlayerRenderStateExtension.CHANGE_INVISIBLE_RENDERER.get() == Boolean.TRUE) {
            model.renderToBuffer(stack, vertex, light, overlay, 0x3FFFFFFF);
        } else {
            original.call(model, stack, vertex, light, overlay);
        }
    }
}
