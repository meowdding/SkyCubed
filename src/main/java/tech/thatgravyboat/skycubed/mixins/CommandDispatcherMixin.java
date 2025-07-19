package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tech.thatgravyboat.skycubed.features.commands.hypixel.HypixelCommands;

import java.util.Collection;

@Mixin(value = CommandDispatcher.class, remap = false)
public abstract class CommandDispatcherMixin<S> {

    @Shadow public abstract Collection<String> getPath(CommandNode<S> target);

    @WrapMethod(method = "findAmbiguities", remap = false)
    private void findAmbiguities(AmbiguityConsumer<S> consumer, Operation<Void> original) {
        // The ambiguity can be ignored for these as they are always forwarded to the server
        original.call((AmbiguityConsumer<S>) (parent, child, sibling, inputs) -> {
            var command = this.getPath(child).iterator();
            if (command.hasNext() && HypixelCommands.INSTANCE.isRootCommand(command.next())) return;
            consumer.ambiguous(parent, child, sibling, inputs);
        });
    }
}
