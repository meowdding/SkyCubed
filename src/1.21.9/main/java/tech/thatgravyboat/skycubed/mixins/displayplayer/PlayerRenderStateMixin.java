package tech.thatgravyboat.skycubed.mixins.displayplayer;

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayerRenderStateExtension;

@Mixin(AvatarRenderState.class)
public class PlayerRenderStateMixin implements DisplayEntityPlayerRenderStateExtension {

    @Unique
    private boolean skycubed$isDisplayEntityPlayer = false;

    @Override
    public void skycubed$setIsDisplayEntityPlayer(boolean value) {
        this.skycubed$isDisplayEntityPlayer = value;
    }

    @Override
    public boolean skycubed$isDisplayEntityPlayer() {
        return this.skycubed$isDisplayEntityPlayer;
    }
}
