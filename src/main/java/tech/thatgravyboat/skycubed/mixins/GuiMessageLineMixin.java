package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.client.GuiMessage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatIdHolder;

@Mixin(GuiMessage.Line.class)
public class GuiMessageLineMixin implements ChatIdHolder {

    @Unique
    private @Nullable String skyblockapi$id;

    @Override
    public @Nullable String skyblockapi$getId() {
        return this.skyblockapi$id;
    }

    @Override
    public void skyblockapi$setId(@Nullable String s) {
        this.skyblockapi$id = s;
    }
}
