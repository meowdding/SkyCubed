package tech.thatgravyboat.skycubed.mixins.displayplayer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayerRenderStateExtension;

@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {

    @WrapOperation(
        method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
        )
    )
    private RenderType onRenderLayers(ResourceLocation location, Operation<RenderType> original) {
        if (DisplayEntityPlayerRenderStateExtension.CHANGE_INVISIBLE_RENDERER.get() == Boolean.TRUE) {
            return RenderType.armorTranslucent(location);
        }
        return original.call(location);
    }

    @WrapOperation(
        method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/EquipmentLayerRenderer;getColorForLayer(Lnet/minecraft/client/resources/model/EquipmentClientInfo$Layer;I)I")
    )
    private int onRenderLayers(EquipmentClientInfo.Layer layer, int color, Operation<Integer> original) {
        color = original.call(layer, color);
        if (DisplayEntityPlayerRenderStateExtension.CHANGE_INVISIBLE_RENDERER.get() == Boolean.TRUE) {
            if (color == 0) {
                return ARGB.color(63, 255, 255, 255);
            } else {
                int r = ARGB.red(color);
                int g = ARGB.green(color);
                int b = ARGB.blue(color);
                return ARGB.color(63, r, g, b);
            }
        }
        return color;
    }
}
