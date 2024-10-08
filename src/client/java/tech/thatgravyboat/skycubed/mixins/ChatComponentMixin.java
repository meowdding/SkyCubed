package tech.thatgravyboat.skycubed.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
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
}
