package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.skycubed.api.accessors.EntityAccessor;

@Mixin(Entity.class)
public class EntityMixin implements EntityAccessor {

    @Unique
    private boolean skycubed$glow;
    @Unique
    private int skycubed$glowColor;

    @Override
    public boolean getSkycubed$glow() {
        return skycubed$glow;
    }

    @Override
    public void setSkycubed$glow(boolean b) {
        this.skycubed$glow = b;
    }

    @Override
    public int getSkycubed$glowColor() {
        return this.skycubed$glowColor;
    }

    @Override
    public void setSkycubed$glowColor(int i) {
        this.skycubed$glowColor = i;
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    public void currentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (skycubed$glow) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    public void getTeamColor(CallbackInfoReturnable<Integer> cir) {
        if (skycubed$glow && skycubed$glowColor != 0) {
            cir.setReturnValue(this.skycubed$glowColor);
        }
    }
}
