package tech.thatgravyboat.skycubed.features.info

import earth.terrarium.olympus.client.ui.context.ContextMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.overlays.EditableProperty
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.api.overlays.RegisterOverlay
import tech.thatgravyboat.skycubed.config.overlays.InfoHudOverlayConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.features.info.farming.FarmhouseInfoOverlay
import tech.thatgravyboat.skycubed.features.info.farming.GardenInfoOverlay
import tech.thatgravyboat.skycubed.features.info.farming.TrapperInfoOverlay
import tech.thatgravyboat.skycubed.features.info.foraging.ParkInfoOverlay
import tech.thatgravyboat.skycubed.features.info.mining.CrystalHollowsInfoOverlay
import tech.thatgravyboat.skycubed.features.info.mining.DwarvesInfoOverlay
import tech.thatgravyboat.skycubed.features.info.mining.GlaciteInfoOverlay
import tech.thatgravyboat.skycubed.features.overlays.vanilla.barDisabled
import tech.thatgravyboat.skycubed.features.overlays.vanilla.disabled
import tech.thatgravyboat.skycubed.mixins.BossHealthOverlayAccessor

@RegisterOverlay
object InfoOverlay : Overlay {

    override val name: Component = Component.literal("Info Overlay")
    override val position: Position = Position()
        get() {
            val bossEvents = (McClient.gui.bossOverlay as? BossHealthOverlayAccessor)?.events
            val modifier: Int = bossEvents.let { events ->
                var modifier = 0
                events?.forEach { event ->
                    if (event.value.disabled) {
                        modifier -= 19
                    } else if (event.value.barDisabled) {
                        modifier -= 5
                    }
                }
                modifier
            }
            val bossOverlayY = bossEvents
                ?.size
                ?.takeIf { it > 0 }
                ?.let { 17 + (it - 1) * 19 + modifier } ?: 0

            val (_, y) = OverlayPositions.info

            field.scale = OverlayPositions.info.scale
            field.y = if (y >= bossOverlayY) y else bossOverlayY
            field.x = (McClient.window.guiScaledWidth - (34 * field.scale).toInt()) / 2

            return field
        }
    override val enabled: Boolean get() = InfoHudOverlayConfig.enabled
    override val properties: Collection<EditableProperty> = setOf(EditableProperty.Y, EditableProperty.SCALE, EditableProperty.MISC)
    override val bounds: Pair<Int, Int> = 34 to 34

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        when (LocationAPI.island) {
            SkyBlockIsland.THE_RIFT -> RiftInfoOverlay.render(graphics)
            SkyBlockIsland.DWARVEN_MINES -> when (LocationAPI.area) {
                SkyBlockAreas.GREAT_LAKE,
                SkyBlockAreas.GLACITE_TUNNELS,
                SkyBlockAreas.BASECAMP,
                SkyBlockAreas.FOSSIL_RESEARCH,
                    -> GlaciteInfoOverlay.render(graphics)

                else -> DwarvesInfoOverlay.render(graphics)
            }

            SkyBlockIsland.CRYSTAL_HOLLOWS -> CrystalHollowsInfoOverlay.render(graphics)
            SkyBlockIsland.THE_BARN -> TrapperInfoOverlay.render(graphics)
            SkyBlockIsland.HUB -> when (LocationAPI.area) {
                SkyBlockAreas.FARMHOUSE -> FarmhouseInfoOverlay.render(graphics)
                else -> MainInfoOverlay.render(graphics)
            }

            SkyBlockIsland.GARDEN -> GardenInfoOverlay.render(graphics)
            SkyBlockIsland.THE_PARK -> ParkInfoOverlay.render(graphics)
            else -> MainInfoOverlay.render(graphics)
        }
    }

    override fun onRightClick() = ContextMenu.open {
        it.dangerButton(Text.of("Reset Position")) {
            OverlayPositions.info.reset()
        }
    }

    override fun setY(y: Int) {
        val height = McClient.window.guiScaledHeight
        if (bounds.second == 0 || bounds.second >= height) return
        OverlayPositions.info.y = if (y < height / 2) y.coerceAtLeast(0) else (y - height).coerceAtMost(-bounds.second)
    }

    override fun setScale(scale: Float) {
        OverlayPositions.info.scale = (scale * 100f).toInt() / 100f
    }
}
