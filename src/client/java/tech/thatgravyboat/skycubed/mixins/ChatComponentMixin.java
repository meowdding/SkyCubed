package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatIdHolder;
import tech.thatgravyboat.skycubed.features.chat.ChatTabColors;

import java.util.List;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;

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

    @Inject(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", shift = At.Shift.AFTER))
    private void addMessageToDisplayQueue(GuiMessage message, CallbackInfo ci) {
        String id = ((ChatIdHolder) (Object) message).skyblockapi$getId();
        if (id != null && !this.trimmedMessages.isEmpty()) {
            ChatIdHolder holder = (ChatIdHolder) (Object) this.trimmedMessages.getFirst();
            assert holder != null;
            holder.skyblockapi$setId(id);
        }
    }
}
