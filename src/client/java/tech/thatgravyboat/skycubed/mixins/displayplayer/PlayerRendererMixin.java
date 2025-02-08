package tech.thatgravyboat.skycubed.mixins.displayplayer;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayer;
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayerRenderStateExtension;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
    private void onExtractRenderState(AbstractClientPlayer player, PlayerRenderState state, float partialTick, CallbackInfo ci) {
        if (state instanceof DisplayEntityPlayerRenderStateExtension extension && player instanceof DisplayEntityPlayer) {
            extension.skycubed$setIsDisplayEntityPlayer(true);
        }
    }
}
