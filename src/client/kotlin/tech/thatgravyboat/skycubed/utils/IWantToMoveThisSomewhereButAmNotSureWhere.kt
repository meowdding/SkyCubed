package tech.thatgravyboat.skycubed.utils

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.shaders.UniformType
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.olympus.client.pipelines.PipelineRenderer
import me.owdding.lib.displays.Display
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.ResourceLocation

class TexturedCircleDisplay(@JvmField val width: Int, @JvmField val height: Int, val texture: ResourceLocation) : Display {
    override fun getHeight(): Int = height

    override fun getWidth(): Int = width

    override fun render(graphics: GuiGraphics) {
        val mc = Minecraft.getInstance()

        val matrix = graphics.pose().last().pose()
        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)

        buffer.addVertex(matrix, 0f, 0f, 0f)
            .setUv(-1f, -1f)
            .setColor((0xFFFFFFFF).toInt())
        buffer.addVertex(matrix, 0f, height.toFloat(), 0f)
            .setUv(-1f, 1f)
            .setColor((0xFFFFFFFF).toInt())
        buffer.addVertex(matrix, width.toFloat(), height.toFloat(), 0f)
            .setUv(1f, 1f)
            .setColor((0xFFFFFFFF).toInt())
        buffer.addVertex(matrix, width.toFloat(), 0f, 0f)
            .setUv(1f, -1f)
            .setColor((0xFFFFFFFF).toInt())

        val sprite = mc.guiSprites.getSprite(texture)

        val abstractTexture: AbstractTexture = mc.textureManager.getTexture(
            sprite.atlasLocation()
        )

        RenderSystem.setShaderTexture(0, abstractTexture.texture)

        PipelineRenderer.draw(renderPipeline, buffer.buildOrThrow()) {
            it.bindSampler("Sampler0", abstractTexture.texture)
            it.setUniform("uvs", sprite.u0, sprite.u1, sprite.v0, sprite.v1)
        }
    }

    private val renderPipeline: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation("pipeline/circle_tex")
            .withFragmentShader(ResourceLocation.fromNamespaceAndPath("skycubed", "circle_tex_color"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withUniform("uvs", UniformType.VEC4)
            .withDepthWrite(false)
            .build(),
    )
}
