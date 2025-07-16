package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.buffers.Std140Builder
import com.mojang.blaze3d.buffers.Std140SizeCalculator
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.shaders.UniformType
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import earth.terrarium.olympus.client.pipelines.renderer.PipelineRenderer
import earth.terrarium.olympus.client.pipelines.uniforms.RenderPipelineUniforms
import earth.terrarium.olympus.client.pipelines.uniforms.RenderPipelineUniformsStorage
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.ResourceLocation
import org.joml.Vector2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import java.nio.ByteBuffer
import java.util.function.Function

class CircularMinimapUniform(
    val resolution: Vector2f,
    val radius: Float,
) : RenderPipelineUniforms {

    companion object {

        const val UNIFORM_NAME = "CircularMinimapUniform"
        val STORAGE = RenderPipelineUniformsStorage.register<CircularMinimapUniform>(
            "SkyCubed Circular Minimap UBO",
            1,
            Std140SizeCalculator().putVec2().putFloat()
        )
    }

    override fun name(): String = UNIFORM_NAME

    override fun write(byteBuffer: ByteBuffer) {
        Std140Builder.intoBuffer(byteBuffer)
            .putVec2(resolution)
            .putFloat(radius)
            .get()
    }

}

data class CircularMinimapPipState(
    val texture: ResourceLocation,
    val circleCenterX: Float,
    val circleCenterY: Float,
    val circleRadius: Float,
    val x: Int,
    val y: Int,
    val uOffset: Float,
    val vOffset: Float,
    val uWidth: Int,
    val vHeight: Int,
    val textureWidth: Int,
    val textureHeight: Int,
    val color: Int = -1,

    val bounds: ScreenRectangle?,
    val scissor: ScreenRectangle?,
) : OlympusPictureInPictureRenderState<CircularMinimapPipState> {

    override fun x0(): Int = x
    override fun x1(): Int = x + uWidth
    override fun y0(): Int = y
    override fun y1(): Int = y + vHeight
    override fun scale(): Float = 1f
    override fun scissorArea(): ScreenRectangle? = null
    override fun bounds(): ScreenRectangle? = bounds

    override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<CircularMinimapPipState>> = Function { buffer ->
        CircularMinimapPipRenderer(buffer)
    }
}

private val MAP_RENDER_PIPELINE: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
        .withLocation(SkyCubed.id("pipeline/circular_map_background"))
        .withFragmentShader(SkyCubed.id("map/circular_map_background"))
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .withUniform(CircularMinimapUniform.UNIFORM_NAME, UniformType.UNIFORM_BUFFER)
        .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
        .withUniform("Projection", UniformType.UNIFORM_BUFFER)
        .withDepthWrite(false)
        .build(),
)

class CircularMinimapPipRenderer(
    buffer: MultiBufferSource.BufferSource
) : PictureInPictureRenderer<CircularMinimapPipState>(buffer) {

    override fun getRenderStateClass(): Class<CircularMinimapPipState> = CircularMinimapPipState::class.java

    override fun renderToTexture(state: CircularMinimapPipState, stack: PoseStack) {
        val bounds = state.bounds ?: return

        val minU = state.uOffset / state.textureWidth
        val maxU = (state.uOffset + state.uWidth) / state.textureWidth
        val minV = state.vOffset / state.textureHeight
        val maxV = (state.vOffset + state.vHeight) / state.textureHeight

        val scale = McClient.window.guiScale.toFloat()
        val scaledWidth = bounds.width() * scale
        val scaledHeight = bounds.height() * scale

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(0f, 0f, 0f).setUv(minU, minV).setColor(-1)
        buffer.addVertex(0f, scaledHeight, 0f).setUv(minU, maxV).setColor(-1)
        buffer.addVertex(scaledWidth, scaledHeight, 0f).setUv(maxU, maxV).setColor(-1)
        buffer.addVertex(scaledWidth, 0f, 0f).setUv(maxU, minV).setColor(-1)

        val texture = McClient.self.textureManager.getTexture(state.texture)
        texture.setFilter(false, false)

        RenderSystem.setShaderTexture(0, texture.textureView)

        PipelineRenderer.builder(MAP_RENDER_PIPELINE, buffer.buildOrThrow())
            .uniform(CircularMinimapUniform.STORAGE, CircularMinimapUniform(
                Vector2f(scaledWidth, scaledHeight),
                state.circleRadius,
            ))
            .draw()
    }

    override fun getTextureLabel(): String = "CircularMinimapPipRenderer"

}
