package tech.thatgravyboat.skycubed.features.map.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

expect object CircularMinimapRenderer {

    fun drawMapPart(
        graphics: GuiGraphics,
        texture: ResourceLocation,
        circleCenterX: Float,
        circleCenterY: Float,
        circleRadius: Float,
        x: Int,
        y: Int,
        uOffset: Float,
        vOffset: Float,
        uWidth: Int,
        vHeight: Int,
        textureWidth: Int,
        textureHeight: Int,
        color: Int = -1,
    )
}
