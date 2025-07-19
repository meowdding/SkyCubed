package tech.thatgravyboat.skycubed.mixins;

import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(SkinManager.class)
public interface SkinManagerInvoker {

    @Invoker
    CompletableFuture<PlayerSkin> callRegisterTextures(UUID id, MinecraftProfileTextures textures);
}
