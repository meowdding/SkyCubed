package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skycubed.features.CooldownManager;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @WrapOperation(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemCooldowns;getCooldownPercent(Lnet/minecraft/world/item/Item;F)F"
            )
    )
    public float onCooldown(ItemCooldowns instance, Item item, float f, Operation<Float> original, @Local(argsOnly = true) ItemStack stack) {
        Float cooldown = CooldownManager.INSTANCE.getCooldown(stack);
        if (cooldown != null) {
            return cooldown;
        }
        return original.call(instance, item, f);
    }

}
