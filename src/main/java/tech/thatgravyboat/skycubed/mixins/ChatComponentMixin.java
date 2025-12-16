package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatIdHolder;
import tech.thatgravyboat.skycubed.features.chat.ChatTabColors;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @WrapOperation(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At(value = "NEW", target = "(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)Lnet/minecraft/client/GuiMessage;")
    )
    private GuiMessage addMessage(int i, Component component, MessageSignature messageSignature, GuiMessageTag guiMessageTag, Operation<GuiMessage> original) {
        GuiMessageTag tag = ChatTabColors.INSTANCE.getChatColor(component);
        return original.call(
                i,
                component,
                messageSignature,
                tag != null ? tag : guiMessageTag
        );
    }

    @WrapOperation(
        method = "addMessageToDisplayQueue",
        at = @At(
            value = "NEW",
            target = "(ILnet/minecraft/util/FormattedCharSequence;Lnet/minecraft/client/GuiMessageTag;Z)Lnet/minecraft/client/GuiMessage$Line;"
        )
    )
    private GuiMessage.Line addMessageToDisplayQueue(
        int addedTime,
        FormattedCharSequence content,
        GuiMessageTag tag,
        boolean endOfEntry,
        Operation<GuiMessage.Line> original,
        @Local(ordinal = 0, argsOnly = true) GuiMessage message
    ) {
        var line = original.call(addedTime, content, tag, endOfEntry);
        String id = ((ChatIdHolder) (Object) message).skyblockapi$getId();
        if (id != null && (Object) line instanceof ChatIdHolder holder) {
            holder.skyblockapi$setId(id);
        }
        return line;
    }
}
