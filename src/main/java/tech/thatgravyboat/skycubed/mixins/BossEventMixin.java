package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skycubed.features.overlays.vanilla.BossEventExtension;

@Mixin(BossEvent.class)
public class BossEventMixin implements BossEventExtension {
    @Unique private boolean disabled = false;
    @Unique private boolean disabledBar = false;

    @Override
    public boolean getSkycubed$disabled() {
        return disabled;
    }

    @Override
    public void setSkycubed$disabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public boolean getSkycubed$barDisabled() {
        return disabledBar;
    }

    @Override
    public void setSkycubed$barDisabled(boolean disabled) {
        this.disabledBar = disabled;
    }
}
