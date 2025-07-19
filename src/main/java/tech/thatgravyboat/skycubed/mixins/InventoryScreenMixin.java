package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement;
import tech.thatgravyboat.skycubed.config.Config;
import tech.thatgravyboat.skycubed.features.equipment.EquipmentManager;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {

    public InventoryScreenMixin(InventoryMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @WrapMethod(method = "showsActiveEffects")
    private boolean showsActiveEffects(Operation<Boolean> original) {
        return !Config.INSTANCE.getHiddenHudElements().contains(HudElement.EFFECTS) && original.call();
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void renderBg(GuiGraphics graphics, float f, int i, int j, CallbackInfo ci) {
        EquipmentManager.INSTANCE.onRenderScreen(
                (InventoryScreen) (Object) this,
                graphics,
                this.leftPos,
                this.topPos,
                i,
                j
        );
    }
}
