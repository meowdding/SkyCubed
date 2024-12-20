package tech.thatgravyboat.skycubed.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Level.class)
public interface LevelInvoker {
    @Invoker
    LevelEntityGetter<Entity> callGetEntities();
}
