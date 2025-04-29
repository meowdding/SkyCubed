package tech.thatgravyboat.skycubed.features.overlays.map

import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.state.MapRenderState
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures.backgroundBox

object DungeonMap {

    private val state = MapRenderState()
    val canRender: Boolean get() = state.texture != null && state.decorations.isNotEmpty() && OverlaysConfig.map.dungeonMap

    fun render(graphics: GuiGraphics) {
        state.texture ?: return

        graphics.blitSprite(
            RenderType::guiTextured, backgroundBox,
            0, 0,
            90, 90
        )

        graphics.blit(
            RenderType::guiTextured, state.texture!!,
            0, 0,
            0f, 0f,
            90, 90,
            128, 128,
            128, 128
        )

        graphics.pushPop {
            for (decoration in state.decorations) {
                translate(0f, 0f, 0.02f)

                graphics.pushPop decoration@ {
                    translate(((decoration.x.toFloat() / 2) + 64f) * 0.703125f, ((decoration.y.toFloat() / 2) + 64f) * 0.703125f, 0f)
                    mulPose(Axis.ZP.rotationDegrees(((decoration.rot + 8) * 360) / 16f))
                    translate(-4f, -4f, 0f)

                    val sprite = decoration.atlasSprite ?: return@decoration

                    graphics.blitSprite(
                        RenderType::guiTextured, sprite,
                        0, 0,
                        8, 8
                    )
                }
            }
        }
    }

    @Subscription
    fun onChange(event: IslandChangeEvent) {
        state.texture = null
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onPacket(event: PacketReceivedEvent) {
        val mapid = (event.packet as? ClientboundMapItemDataPacket)?.mapId?.takeIf { it.id == 1024 } ?: return
        McClient.tell {
            McLevel.self.getMapData(mapid)?.let { data ->
                McClient.self.mapRenderer.extractRenderState(mapid, data, state)
            }
        }
    }

}