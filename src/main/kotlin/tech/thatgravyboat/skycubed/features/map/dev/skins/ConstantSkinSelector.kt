package tech.thatgravyboat.skycubed.features.map.dev.skins

import net.minecraft.world.entity.Entity

data class ConstantSkinSelector(val skin: String) : SkinSelector<Entity> {
    override fun getSkin(entity: Entity) = skin

    companion object {
        val COW = ConstantSkinSelector("http://textures.minecraft.net/texture/87e7193a0c3ff82c18927d5a73015f057d087ff92c4bce1557be4623d30950ff")
        val MUSHROOM_COW = ConstantSkinSelector("http://textures.minecraft.net/texture/1543b72def1b247685ad4d027df86c9632e7dac143a9552ec89c80035c3ba4ae")
        val WITCH = ConstantSkinSelector("http://textures.minecraft.net/texture/fce6604157fc4ab5591e4bcf507a749918ee9c41e357d47376e0ee7342074c90")
        val SKELETON = ConstantSkinSelector("http://textures.minecraft.net/texture/482b78da6ee713d5acfe5fcb0754ee56900831a5098313064108de6e7e406839")
        val WITHER_SKELETON = ConstantSkinSelector("http://textures.minecraft.net/texture/1e4d204ebc242eca2148f5853e3af00f84f0d674099dc394f6d2924b240ca2e3")
        val MAGMA_CUBE = ConstantSkinSelector("http://textures.minecraft.net/texture/a1c97a06efde04d00287bf20416404ab2103e10f08623087e1b0c1264a1c0f0c")
    }
}
