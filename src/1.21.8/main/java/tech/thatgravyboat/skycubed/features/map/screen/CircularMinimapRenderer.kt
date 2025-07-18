package tech.thatgravyboat.skycubed.features.map.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.resources.ResourceLocation


actual object CircularMinimapRenderer {

    actual fun drawMapPart(
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
        color: Int,
    ) {
        val bounds = ScreenRectangle(x, y, uWidth, vHeight).transformAxisAligned(graphics.pose())

        val minU = uOffset / textureWidth
        val maxU = (uOffset + uWidth) / textureWidth
        val minV = vOffset / textureHeight
        val maxV = (vOffset + vHeight) / textureHeight

        graphics.guiRenderState.submitPicturesInPictureState(
            CircularMinimapPipState(
                texture,
                circleCenterX,
                circleCenterY,
                circleRadius,
                bounds.position.x,
                bounds.position.y,
                bounds.position.x + bounds.width,
                bounds.position.y + bounds.height,
                minU,
                minV,
                maxU,
                maxV,
                textureWidth,
                textureHeight,
                color,
                bounds,
                graphics.scissorStack.peek(),
            ),
        )
    }
}
