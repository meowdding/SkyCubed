package tech.thatgravyboat.skycubed.utils

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.GpuTextureView
import com.mojang.blaze3d.vertex.PoseStack
import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.gui.render.state.BlitRenderState
import net.minecraft.client.gui.render.state.GuiRenderState
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.util.Mth
import net.minecraft.world.entity.EquipmentSlot
import org.joml.Matrix3x2f
import org.joml.Quaternionf
import org.joml.Vector3f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.config.overlays.PlayerDisplay
import tech.thatgravyboat.skycubed.config.overlays.RpgOverlayConfig
import tech.thatgravyboat.skycubed.features.overlays.rpg.RpgOverlayPositionHandler
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import net.minecraft.client.renderer.entity.state.PlayerRenderState as AvatarRenderState
import net.minecraft.world.entity.player.Player as Avatar

class RpgPlayerRenderer(buffer: MultiBufferSource.BufferSource) : PictureInPictureRenderer<RpgPlayerRenderer.State>(buffer) {

    private var textureView: GpuTextureView? = null

    override fun getRenderStateClass(): Class<State> = State::class.java

    override fun renderToTexture(state: State, stack: PoseStack) {
        this.textureView = RenderSystem.outputColorTextureOverride // Internally before this method is called, the texture is set to the output color texture.

        val dispatcher = McClient.self.entityRenderDispatcher
        val renderer = McClient.self.gameRenderer

        renderer.lighting.setupFor(Lighting.Entry.ENTITY_IN_UI)
        stack.translate(state.translation.x, state.translation.y, state.translation.z)
        stack.mulPose(state.rotation)
        if (state.cameraAngle != null) {
            val rotation = state.cameraAngle.conjugate(Quaternionf()).rotateY(Mth.PI)
            dispatcher.overrideCameraOrientation(rotation)
        }
        dispatcher.setRenderShadow(false)
        dispatcher.render(state.state, 0.0, 0.0, 0.0, stack, this.bufferSource, 15728880)
        dispatcher.setRenderShadow(true)
    }

    override fun blitTexture(state: State, gui: GuiRenderState) {
        val mask = McClient.self.textureManager.getTexture(SkyCubed.id("textures/gui/sprites/rpg/mask.png"))

        gui.submitBlitToCurrentLayer(
            BlitRenderState(
                pipeline,
                TextureSetup.doubleTexture(this.textureView!!, mask.textureView),
                state.pose(),
                state.x0(), state.y0(), state.x1(), state.y1(),
                0.0F, 1.0F, 1.0F, 0.0F,
                -1, state.scissorArea(), null,
            ),
        )
    }

    override fun getTextureLabel(): String = "rpg_player_pip"

    data class State(
        val state: EntityRenderState,
        val translation: Vector3f, val rotation: Quaternionf, val cameraAngle: Quaternionf?,
        val x0: Int, val y0: Int, val x1: Int, val y1: Int,
        val scale: Float,
        val scissor: ScreenRectangle?, val bounds: ScreenRectangle?,
        val pose: Matrix3x2f,
    ) : OlympusPictureInPictureRenderState<State> {

        override fun getFactory(): Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<State>> =
            Function { RpgPlayerRenderer(it) }

        override fun x0(): Int = x0
        override fun x1(): Int = x1
        override fun y0(): Int = y0
        override fun y1(): Int = y1
        override fun scale(): Float = scale
        override fun scissorArea(): ScreenRectangle? = scissor
        override fun bounds(): ScreenRectangle? = bounds
        override fun pose(): Matrix3x2f = pose

        override fun hashCode(): Int {
            return State::class.java.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return other is State
        }
    }

    companion object {

        private val pipeline = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(SkyCubed.id("pipeline/gui_rpg_player"))
            .withFragmentShader(SkyCubed.id("rpg_player"))
            .withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)
            .withSampler("Sampler0")
            .withSampler("Sampler1")
            .build()

        // Height of the player, bc we cant use boundingbox (sneaking my beloved)
        private const val PLAYER_HEIGHT = 1.8f


        @Suppress("UNCHECKED_CAST")
        fun createNewState(entity: AbstractClientPlayer): EntityRenderState {
            val playerOptions = RpgOverlayPositionHandler.positions.player

            val armor = if (RpgOverlayConfig.playerDisplay == PlayerDisplay.ARMORED) {
                listOf(
                    entity.getItemBySlot(EquipmentSlot.HEAD).copy(),
                    entity.getItemBySlot(EquipmentSlot.CHEST).copy(),
                    entity.getItemBySlot(EquipmentSlot.LEGS).copy(),
                    entity.getItemBySlot(EquipmentSlot.FEET).copy(),
                )
            } else {
                emptyList()
            }


            val fakeplayer = DisplayEntityPlayer(CompletableFuture.completedFuture(entity.skin), armor, false)
            val renderer = McClient.self.entityRenderDispatcher.getRenderer(fakeplayer) as EntityRenderer<Avatar, AvatarRenderState>
            val state = renderer.createRenderState()
            renderer.extractRenderState(fakeplayer as Avatar, state, 1f)

            state.hitboxesRenderState = null
            state.x = 0.0
            state.y = 0.0
            state.z = 0.0
            //? if > 1.21.8 {
            state.isVisuallySwimming = false
            //?} else
            /*state.swinging = false*/
            state.attackTime = 0f
            state.walkAnimationPos = 0f
            state.walkAnimationSpeed = 0f
            state.wornHeadAnimationPos = 0f
            state.ageInTicks = 0f
            state.yRot = 0f
            state.bodyRot = 180f + playerOptions.xRot
            state.xRot = playerOptions.yRot
            state.isCrouching = false

            return state
        }

        fun draw(
            graphics: GuiGraphics,
            entity: AbstractClientPlayer,
            x: Int, y: Int, width: Int, height: Int, scale: Float,
        ) {
            graphics.nextStratum()

            val centerY = height / 2f

            val baseRotation = Quaternionf().rotateZ(Math.PI.toFloat())
            val tiltRotation = Quaternionf()

            baseRotation.mul(tiltRotation)
            val entityScale = entity.scale
            val scaledSize = scale / entityScale
            val positionOffset = Vector3f(0.1f, (-centerY / scaledSize) + PLAYER_HEIGHT * entityScale * 0.8f, 0.0f)

            val state = State(
                createNewState(entity),
                positionOffset, baseRotation, tiltRotation,
                x, y, x + width, y + height,
                scale,
                graphics.scissorStack.peek(),
                PictureInPictureRenderState.getBounds(x, y, x + width, y + height, graphics.scissorStack.peek()),
                Matrix3x2f(graphics.pose()),
            )
            graphics.guiRenderState.submitPicturesInPictureState(state)
        }
    }
}

