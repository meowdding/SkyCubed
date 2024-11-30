package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skycubed.features.tablist.CompactTablist;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(GuiGraphics graphics, int width, Scoreboard scoreboard, @Nullable Objective objective, CallbackInfo ci) {
        if (CompactTablist.INSTANCE.renderCompactTablist(graphics)) ci.cancel();
    }
}
