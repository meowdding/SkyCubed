package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.shaders.UniformType
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.olympus.client.pipelines.PipelineRenderer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed.id

object CircularMinimapRenderer {
    fun drawMapPart(
        graphics: GuiGraphics,
        texture: ResourceLocation,
        circleCenterX: Float,
        circleCenterY: Float,
        circleRadius: Float,
        x1: Int,
        y1: Int,
        uOffset: Float,
        vOffset: Float,
        uWidth: Int,
        vHeight: Int,
        textureWidth: Int,
        textureHeight: Int,
        color: Int = -1,
    ) {
        val x2 = x1 + uWidth
        val y2 = y1 + vHeight

        val minU = (uOffset + 0.0f) / textureWidth
        val maxU = (uOffset + uWidth) / textureWidth
        val minV = (vOffset + 0.0f) / textureHeight
        val maxV = (vOffset + vHeight) / textureHeight

        val matrix = graphics.pose().last().pose()
        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)

        buffer.addVertex(matrix, x1.toFloat(), x1.toFloat(), 0f)
            .setUv(minU, minV)
            .setColor(color)
        buffer.addVertex(matrix, x1.toFloat(), y2.toFloat(), 0f)
            .setUv(minU, maxV)
            .setColor(color)
        buffer.addVertex(matrix, x2.toFloat(), y2.toFloat(), 0f)
            .setUv(maxU, maxV)
            .setColor(color)
        buffer.addVertex(matrix, x2.toFloat(), x1.toFloat(), 0f)
            .setUv(maxU, minV)
            .setColor(color)

        val gpuTexture: GpuTexture = McClient.self.textureManager.getTexture(texture).texture

        RenderSystem.setShaderTexture(0, gpuTexture)

        PipelineRenderer.draw(MAP_RENDER_PIPELINE, buffer.buildOrThrow()) {
            it.setUniform("Scale", McClient.window.guiScale.toFloat())
            it.setUniform(
                "CirclePosition",
                circleCenterX,
                circleCenterY,
            )
            it.setUniform("Radius", circleRadius)
        }
    }

    private val MAP_RENDER_PIPELINE: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(id( "pipeline/minimap"))
            .withFragmentShader(id("minimap_position_tex_color"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withUniform("CirclePosition", UniformType.VEC2)
            .withUniform("ScreenSize", UniformType.VEC2)
            .withUniform("Radius", UniformType.FLOAT)
            .withUniform("Scale", UniformType.FLOAT)
            .withDepthWrite(false)
            .build(),
    )
}
