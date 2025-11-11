package tech.thatgravyboat.skycubed.mixins.displayplayer;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayer;
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayerRenderStateExtension;

@Mixin(AvatarRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void onExtractRenderState(Avatar entity, AvatarRenderState renderState, float partial, CallbackInfo ci) {
        if (renderState instanceof DisplayEntityPlayerRenderStateExtension extension) {
            extension.skycubed$setIsDisplayEntityPlayer(entity instanceof DisplayEntityPlayer);
        }
    }
}
