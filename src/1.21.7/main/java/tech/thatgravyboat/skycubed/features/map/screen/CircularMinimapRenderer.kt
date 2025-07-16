package tech.thatgravyboat.skycubed.features.map.screen

import earth.terrarium.olympus.client.pipelines.RoundedRectangle
import net.minecraft.client.gui.GuiGraphics
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
        RoundedRectangle.draw(
            graphics,
            0, 0, 100, 100,
            -1, -1, 50f, 0
        )




//         graphics.guiRenderState.submitPicturesInPictureState(CircularMinimapPipState(
//             texture, circleCenterX, circleCenterY, circleRadius, 0, 0, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight, color,
//             OlympusPictureInPictureRenderState.getRelativeBounds(graphics, 0, 0, uWidth, vHeight),
//             graphics.scissorStack.peek()
//         ))
    }
}
