package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skycubed.config.rendering.RenderingConfig;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @ModifyReturnValue(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("RETURN"))
    public boolean modify(boolean original, @Local(argsOnly = true) LivingEntity livingEntity) {
        if (RenderingConfig.INSTANCE.getShowOwnTag() && livingEntity instanceof LocalPlayer) {
            return true;
        }

        return original;
    }

}
