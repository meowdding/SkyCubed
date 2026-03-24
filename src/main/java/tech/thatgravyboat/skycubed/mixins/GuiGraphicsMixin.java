package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skycubed.features.items.CooldownManager;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsMixin {

    @WrapOperation(
            method = "itemCooldown",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemCooldowns;getCooldownPercent(Lnet/minecraft/world/item/ItemStack;F)F"
            )
    )
    public float onCooldown(ItemCooldowns instance, ItemStack item, float f, Operation<Float> original) {
        Float cooldown = CooldownManager.INSTANCE.getCooldown(item);
        if (cooldown != null) {
            return cooldown;
        }
        return original.call(instance, item, f);
    }

}
