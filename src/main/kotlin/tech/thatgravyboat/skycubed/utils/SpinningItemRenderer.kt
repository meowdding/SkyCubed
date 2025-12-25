package tech.thatgravyboat.skycubed.utils

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import me.owdding.lib.rendering.MeowddingPipState
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Matrix3x2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import java.util.function.Function

class SpinningItemRenderer(buffer: MultiBufferSource.BufferSource) : PictureInPictureRenderer<SpinningItemRenderState>(buffer) {

    override fun getRenderStateClass(): Class<SpinningItemRenderState> = SpinningItemRenderState::class.java

    override fun renderToTexture(renderState: SpinningItemRenderState, poseStack: PoseStack) {
        poseStack.pushPop {
            with(renderState) {
                poseStack.translate(0f, bounds.height() / -2f - 5f, 0f)
                poseStack.scale(scale, scale, scale)
                poseStack.mulPose(Axis.XP.rotationDegrees(System.currentTimeMillis() % xSpeed * 100))
                poseStack.mulPose(Axis.YP.rotationDegrees(System.currentTimeMillis() % ySpeed * 100))
                poseStack.mulPose(Axis.ZP.rotationDegrees(System.currentTimeMillis() % zSpeed * 100))

                McClient.self.gameRenderer.lighting.setupFor(if (item.usesBlockLight()) Lighting.Entry.ITEMS_3D else Lighting.Entry.ITEMS_FLAT)

                item.submit(
                    poseStack,
                    McClient.self.gameRenderer.featureRenderDispatcher.submitNodeStorage,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    0,
                )
            }
        }
    }

    override fun getTextureLabel(): String = "skycubed_spinning_item"
}

data class SpinningItemRenderState(
    val item: TrackingItemStackRenderState,
    val xSpeed: Float = 0f,
    val ySpeed: Float = 0f,
    val zSpeed: Float = 0f,
    override val scale: Float = 20f,
    override val bounds: ScreenRectangle,
    override val scissorArea: ScreenRectangle?,
    override val pose: Matrix3x2f,
) : MeowddingPipState<SpinningItemRenderState>() {
    constructor(
        item: ItemStack,
        xSpeed: Float = 0f,
        ySpeed: Float = 0f,
        zSpeed: Float = 0f,
        scale: Float = 20f,
        bounds: ScreenRectangle,
        scissorArea: ScreenRectangle?,
        pose: Matrix3x2f,
    ) : this(
        TrackingItemStackRenderState().apply {
            McClient.self.itemModelResolver.updateForTopItem(this, item, ItemDisplayContext.NONE, McLevel.self, null, 0)
        },
        xSpeed,
        ySpeed,
        zSpeed,
        scale,
        bounds,
        scissorArea,
        pose,
    )

    override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<SpinningItemRenderState>> =
        Function { buffer -> SpinningItemRenderer(buffer) }

    override val shrinkToScissor: Boolean
        get() = false

    override val x0: Int = bounds.left()
    override val x1: Int = bounds.right()
    override val y0: Int = bounds.top()
    override val y1: Int = bounds.bottom()
}
