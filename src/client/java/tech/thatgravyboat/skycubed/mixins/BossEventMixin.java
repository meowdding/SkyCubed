package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skycubed.features.overlays.BossEventExtension;

@Mixin(BossEvent.class)
public class BossEventMixin implements BossEventExtension {
    @Unique private boolean disabled = false;
    @Unique private boolean disabledBar = false;

    @Override
    public boolean getDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean getBarDisabled() {
        return disabledBar;
    }

    @Override
    public void setBarDisabled(boolean disabled) {
        this.disabledBar = disabled;
    }
}
