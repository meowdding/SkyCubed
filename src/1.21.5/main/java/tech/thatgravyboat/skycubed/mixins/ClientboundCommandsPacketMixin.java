package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skycubed.features.commands.hypixel.HypixelCommands;

@Mixin(ClientboundCommandsPacket.class)
public class ClientboundCommandsPacketMixin {

    @ModifyReturnValue(method = "getRoot", at = @At("RETURN"))
    private RootCommandNode<?> onGetRoot(RootCommandNode<?> original) {
        HypixelCommands.INSTANCE.removeServerCommands(original);
        return original;
    }
}
