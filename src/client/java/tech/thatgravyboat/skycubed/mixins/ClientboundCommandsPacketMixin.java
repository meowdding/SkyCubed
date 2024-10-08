package tech.thatgravyboat.skycubed.mixins;

import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.skycubed.features.commands.hypixel.HypixelCommands;

@Mixin(ClientboundCommandsPacket.class)
public class ClientboundCommandsPacketMixin {

    @Inject(method = "getRoot", at = @At("RETURN"))
    private void onGetRoot(CallbackInfoReturnable<RootCommandNode<SharedSuggestionProvider>> cir) {
        HypixelCommands.INSTANCE.removeServerCommands(cir.getReturnValue());
    }
}
