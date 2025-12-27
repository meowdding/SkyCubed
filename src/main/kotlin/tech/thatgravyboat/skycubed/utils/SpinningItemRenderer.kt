package tech.thatgravyboat.skycubed.utils

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import me.owdding.lib.compat.meowdding.MeowddingFeatures.features
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
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import java.util.function.Function

class SpinningItemRenderer(buffer: MultiBufferSource.BufferSource) : PictureInPictureRenderer<SpinningItemRenderState>(buffer) {

    override fun getRenderStateClass(): Class<SpinningItemRenderState> = SpinningItemRenderState::class.java

    override fun renderToTexture(state: SpinningItemRenderState, stack: PoseStack) {
        stack.pushPop {
            stack.translate(0f, state.bounds.height() / -2f - 3f, 0f)
            stack.scale(20f, 20f, 20f)

            stack.mulPose(Axis.ZN.rotationDegrees(180f))
            stack.mulPose(Axis.XP.rotationDegrees(-30f))
            stack.mulPose(Axis.YP.rotationDegrees(225f))

            val seconds = TickEvent.ticks / 20f
            if (state.xSpeed != 0) stack.mulPose(Axis.XP.rotationDegrees(45f + (seconds * state.xSpeed.toFloat()).toInt() % 360))
            if (state.ySpeed != 0) stack.mulPose(Axis.YP.rotationDegrees(45f + (seconds * state.ySpeed.toFloat()).toInt() % 360))
            if (state.zSpeed != 0) stack.mulPose(Axis.ZP.rotationDegrees(45f + (seconds * state.zSpeed.toFloat()).toInt() % 360))

            val item = state.item
            val features = McClient.self.gameRenderer.featureRenderDispatcher

            McClient.self.gameRenderer.lighting.setupFor(if (item.usesBlockLight()) Lighting.Entry.ITEMS_3D else Lighting.Entry.ITEMS_FLAT)

            item.submit(stack, features.submitNodeStorage, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0)
            features.renderAllFeatures()
        }
    }

    override fun getTextureLabel(): String = "skycubed_spinning_item"
}

data class SpinningItemRenderState(
    val item: TrackingItemStackRenderState,
    val xSpeed: Int = 0,
    val ySpeed: Int = 0,
    val zSpeed: Int = 0,
    override val scale: Float = 1f,
    override val bounds: ScreenRectangle,
    override val scissorArea: ScreenRectangle?,
    override val pose: Matrix3x2f,
) : MeowddingPipState<SpinningItemRenderState>() {
    constructor(
        item: ItemStack,
        xSpeed: Int = 0,
        ySpeed: Int = 0,
        zSpeed: Int = 0,
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
        Matrix3x2f(pose),
    )

    override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<SpinningItemRenderState>> =
        Function { buffer -> SpinningItemRenderer(buffer) }

    override val shrinkToScissor: Boolean get() = false
    override val x0: Int = bounds.left()
    override val x1: Int = bounds.right()
    override val y0: Int = bounds.top()
    override val y1: Int = bounds.bottom()
}
