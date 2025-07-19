package tech.thatgravyboat.skycubed.features.map.dev.skins

import net.minecraft.world.entity.animal.sheep.Sheep
import net.minecraft.world.item.DyeColor

object SheepSkinSelector : SkinSelector<Sheep> {
    override fun getSkin(entity: Sheep): String = when (entity.color) {
        DyeColor.WHITE -> "http://textures.minecraft.net/texture/84e5cdb0edb362cb454586d1fd0ebe971423f015b0b1bfc95f8d5af8afe7e810"
        else -> ":("
    }
}
