package tech.thatgravyboat.skycubed.features.map.dev.skins

import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.Entity

//? if > 1.21.10 {
import net.minecraft.world.entity.animal.cow.Cow
import net.minecraft.world.entity.animal.cow.MushroomCow
import net.minecraft.world.entity.animal.sheep.Sheep
import net.minecraft.world.entity.monster.MagmaCube
import net.minecraft.world.entity.monster.Witch
import net.minecraft.world.entity.monster.skeleton.Skeleton
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.entity.player.Player
//?} else {
 /*import net.minecraft.world.entity.animal.Cow
import net.minecraft.world.entity.animal.MushroomCow
import net.minecraft.world.entity.animal.sheep.Sheep
import net.minecraft.world.entity.monster.MagmaCube
import net.minecraft.world.entity.monster.Skeleton
import net.minecraft.world.entity.monster.Witch
import net.minecraft.world.entity.monster.WitherSkeleton
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
*///?}

interface SkinSelector<E : Entity> {
    fun getSkin(entity: E): String

    operator fun <T : Any> ResourceKey<T>.contains(other: Holder<T>): Boolean {
        return other.`is`(this)
    }

    companion object {
        fun getSkin(entity: Entity): String {

            fun <T : Entity> withSelector(selector: SkinSelector<T>): String {
                @Suppress("UNCHECKED_CAST")
                return selector.getSkin(entity as T)
            }
            return when (entity) {
                is Player -> withSelector(PlayerSkinSelector)
                is Villager -> withSelector(VillagerSkin)
                is Sheep -> withSelector(SheepSkinSelector)
                is Cow -> withSelector(ConstantSkinSelector.COW)
                is MushroomCow -> withSelector(ConstantSkinSelector.MUSHROOM_COW)
                is Witch -> withSelector(ConstantSkinSelector.WITCH)
                is Skeleton -> withSelector(ConstantSkinSelector.SKELETON)
                is WitherSkeleton -> withSelector(ConstantSkinSelector.WITHER_SKELETON)
                is MagmaCube -> withSelector(ConstantSkinSelector.MAGMA_CUBE)
                else -> "Unknown Type"
            }
        }
    }
}
