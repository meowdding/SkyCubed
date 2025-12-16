package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.buffers.Std140Builder
import com.mojang.blaze3d.buffers.Std140SizeCalculator
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.shaders.UniformType
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import earth.terrarium.olympus.client.pipelines.renderer.PipelineRenderer
import earth.terrarium.olympus.client.pipelines.uniforms.RenderPipelineUniforms
import earth.terrarium.olympus.client.pipelines.uniforms.RenderPipelineUniformsStorage
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import org.joml.Vector2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import java.nio.ByteBuffer
import java.util.function.Function

class CircularMinimapUniform(
    val resolution: Vector2f,
    val offset: Vector2f,
    val center: Vector2f,
    val screen: Vector2f,
    val radius: Float,
) : RenderPipelineUniforms {

    companion object {

        const val UNIFORM_NAME = "SkyCubedCircularMinimapUniform"
        val STORAGE = RenderPipelineUniformsStorage.register<CircularMinimapUniform>(
            "SkyCubed Circular Minimap UBO",
            1,
            Std140SizeCalculator()
                .putVec2()
                .putVec2()
                .putVec2()
                .putVec2()
                .putFloat(),
        )
    }

    override fun name(): String = UNIFORM_NAME

    override fun write(byteBuffer: ByteBuffer) {
        Std140Builder.intoBuffer(byteBuffer)
            .putVec2(resolution)
            .putVec2(offset)
            .putVec2(center)
            .putVec2(screen)
            .putFloat(radius)
            .get()
    }
}

data class CircularMinimapPipState(
    val texture: Identifier,
    val circleCenterX: Float,
    val circleCenterY: Float,
    val circleRadius: Float,
    val x0: Int,
    val y0: Int,
    val x1: Int,
    val y1: Int,
    val u0: Float,
    val v0: Float,
    val u1: Float,
    val v1: Float,
    val textureWidth: Int,
    val textureHeight: Int,
    val color: Int = -1,

    val bounds: ScreenRectangle,
    val scissor: ScreenRectangle?,
) : OlympusPictureInPictureRenderState<CircularMinimapPipState> {

    override fun x0(): Int = x0
    override fun x1(): Int = x1
    override fun y0(): Int = y0
    override fun y1(): Int = y1
    override fun scale(): Float = 1f
    override fun scissorArea(): ScreenRectangle? = null
    override fun bounds(): ScreenRectangle = bounds

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
    buffer: MultiBufferSource.BufferSource,
) : PictureInPictureRenderer<CircularMinimapPipState>(buffer) {

    override fun getRenderStateClass(): Class<CircularMinimapPipState> = CircularMinimapPipState::class.java

    override fun renderToTexture(state: CircularMinimapPipState, stack: PoseStack) {
        val bounds = state.bounds

        val scale = McClient.window.guiScale.toFloat()

        val u0 = state.u0
        val u1 = state.u1
        val v0 = state.v0
        val v1 = state.v1

        val x1 = bounds.width * scale
        val y1 = bounds.height * scale

        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        buffer.addVertex(0f, 0f, 0f).setUv(u0, v0).setColor(-1)
        buffer.addVertex(0f, y1, 0f).setUv(u0, v1).setColor(-1)
        buffer.addVertex(x1, y1, 0f).setUv(u1, v1).setColor(-1)
        buffer.addVertex(x1, 0f, 0f).setUv(u1, v0).setColor(-1)

        val texture = McClient.self.textureManager.getTexture(state.texture)

        PipelineRenderer.builder(MAP_RENDER_PIPELINE, buffer.buildOrThrow())
            .textures(TextureSetup.singleTexture(texture.textureView, texture.sampler))
            .uniform(
                CircularMinimapUniform.STORAGE,
                CircularMinimapUniform(
                    Vector2f(bounds.width.toFloat(), bounds.height.toFloat()),
                    Vector2f(bounds.position.x.toFloat(), bounds.position.y.toFloat()),
                    Vector2f(state.circleCenterX, state.circleCenterY),
                    Vector2f(McClient.window.width.toFloat(), McClient.window.height.toFloat()),
                    state.circleRadius,
                ),
            )
            .draw()
    }

    override fun getTextureLabel(): String = "CircularMinimapPipRenderer"

}
