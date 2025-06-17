package tech.thatgravyboat.skycubed.utils

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.shaders.UniformType
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import earth.terrarium.olympus.client.pipelines.PipelineRenderer
import me.owdding.lib.displays.Display
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import tech.thatgravyboat.skycubed.SkyCubed

class NGonDisplay(@JvmField val width: Int, @JvmField val height: Int, val sides: Int, private val display: Display) : Display {

    override fun getHeight(): Int = height

    override fun getWidth(): Int = width

    override fun render(graphics: GuiGraphics) {
        val matrix = graphics.pose().last().pose()
        val buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        buffer.addVertex(matrix, 0f, 0f, 0f).setUv(-1f, -1f)
        buffer.addVertex(matrix, 0f, height.toFloat(), 0f).setUv(-1f, 1f)
        buffer.addVertex(matrix, width.toFloat(), height.toFloat(), 0f).setUv(1f, 1f)
        buffer.addVertex(matrix, width.toFloat(), 0f, 0f).setUv(1f, -1f)

        display.render(graphics)

        PipelineRenderer.draw(renderPipeline, buffer.buildOrThrow()) {
            it.setUniform("Sides", sides)
            it.setUniform("WindowWidthHeight", width.toFloat(), height.toFloat())
        }
    }

    companion object {
        private val renderPipeline: RenderPipeline = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
                .withLocation(SkyCubed.id("pipeline/n-gon.fsh"))
                .withFragmentShader(SkyCubed.id("n-gon"))
                .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withUniform("Sides", UniformType.INT)
                .withUniform("WindowWidthHeight", UniformType.VEC2)
                .withDepthWrite(false)
                .build(),
        )
    }
}
