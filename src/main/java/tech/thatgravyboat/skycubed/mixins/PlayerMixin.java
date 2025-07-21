package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.skycubed.utils.ContributorData;
import tech.thatgravyboat.skycubed.utils.ContributorHandler;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "decorateDisplayNameComponent", at = @At("HEAD"))
    public void modifyPlayerName(MutableComponent displayName, CallbackInfoReturnable<MutableComponent> cir) {
        final ContributorData contributorData = ContributorHandler.INSTANCE.getContributors().get(uuid);
        if (contributorData == null) {
            return;
        }

        displayName.append(" ").append(Component.literal(contributorData.getSymbol()));
    }

}
