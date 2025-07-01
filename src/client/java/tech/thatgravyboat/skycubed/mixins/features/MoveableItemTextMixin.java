package tech.thatgravyboat.skycubed.mixins.features;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skycubed.config.overlays.ItemTextOverlayConfig;
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions;
import tech.thatgravyboat.skycubed.config.overlays.Position;
import tech.thatgravyboat.skycubed.features.overlays.vanilla.MovableItemText;
import tech.thatgravyboat.skycubed.utils.ExtensionsKt;

@Mixin(Gui.class)
public abstract class MoveableItemTextMixin {

    @Unique
    private void drawBackground(GuiGraphics graphics, int x, int y, int width, int color) {
        var radius = ItemTextOverlayConfig.INSTANCE.getRadius();
        int bg = ARGB.multiply(ItemTextOverlayConfig.INSTANCE.getColor(), color);
        if (bg != 0) {
            ExtensionsKt.fillRect(graphics, x - 3, y - 3, width + 6, 15, bg, bg, 0, radius);
        }
    }

    @WrapOperation(method = "renderSelectedItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)I"))
    private int wrapRenderSelectedItemName(GuiGraphics graphics, Font font, Component text, int x, int y, int width, int color, Operation<Integer> original) {
        if (ItemTextOverlayConfig.INSTANCE.getHidden()) return 0;

        if (!MovableItemText.INSTANCE.getEnabled()) {
            this.drawBackground(graphics, x, y, width, color);
            return graphics.drawString(font, text, x, y, color, true);
        }

        PoseStack stack = graphics.pose();
        stack.pushPose();

        Position position = OverlayPositions.INSTANCE.getItemtext();
        float scale = position.getScale();

        stack.translate(position.component1(), position.component2(), 0);
        stack.scale(scale, scale, 1f);

        int alignment = switch (ItemTextOverlayConfig.INSTANCE.getAlignment()) {
            case END -> MovableItemText.WIDTH - width;
            case CENTER -> (MovableItemText.WIDTH - width) / 2;
            case START -> 0;
        };

        this.drawBackground(graphics, alignment, 0, width, color);

        int result = graphics.drawString(font, text, alignment, 0, color, true);

        stack.popPose();
        return result;
    }

}
