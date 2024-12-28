package tech.thatgravyboat.skycubed.features.info

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.features.info.farming.FarmhouseInfoOverlay
import tech.thatgravyboat.skycubed.features.info.farming.GardenInfoOverlay
import tech.thatgravyboat.skycubed.features.info.farming.TrapperInfoOverlay
import tech.thatgravyboat.skycubed.features.info.mining.CrystalHollowsInfoOverlay
import tech.thatgravyboat.skycubed.features.info.mining.DwarvesInfoOverlay
import tech.thatgravyboat.skycubed.features.info.mining.GlaciteInfoOverlay
import tech.thatgravyboat.skycubed.mixins.BossHealthOverlayAccessor
import tech.thatgravyboat.skycubed.utils.Rect

object InfoOverlay : Overlay {

    override val name: Component = Component.literal("Info Overlay")
    override val position: Position = Position()
    override val enabled: Boolean get() = OverlaysConfig.info.enabled
    override val moveable: Boolean = false
    override val bounds: Pair<Int, Int> = 34 to 34
    override val editBounds: Rect = Rect(position.x, position.y, 34, 34)

    private fun getY(): Int {
        val bossOverlay = McClient.gui.bossOverlay as? BossHealthOverlayAccessor ?: return 0
        if (bossOverlay.events.isEmpty()) return 0
        return 17 + (bossOverlay.events.size - 1) * 19
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        editBounds.x = (McClient.window.guiScaledWidth - editBounds.width) / 2
        position.y = getY()
        editBounds.y = position.y

        when (LocationAPI.island) {
            SkyBlockIsland.THE_RIFT -> RiftInfoOverlay.render(graphics)
            SkyBlockIsland.DWARVEN_MINES -> when (LocationAPI.area) {
                SkyBlockAreas.GREAT_LAKE,
                SkyBlockAreas.GLACITE_TUNNELS,
                SkyBlockAreas.BASECAMP,
                SkyBlockAreas.FOSSIL_RESEARCH -> GlaciteInfoOverlay.render(graphics)
                else -> DwarvesInfoOverlay.render(graphics)
            }
            SkyBlockIsland.CRYSTAL_HOLLOWS -> CrystalHollowsInfoOverlay.render(graphics)
            SkyBlockIsland.THE_BARN -> TrapperInfoOverlay.render(graphics)
            SkyBlockIsland.HUB -> when (LocationAPI.area) {
                SkyBlockAreas.FARMHOUSE -> FarmhouseInfoOverlay.render(graphics)
                else -> MainInfoOverlay.render(graphics)
            }
            SkyBlockIsland.GARDEN -> GardenInfoOverlay.render(graphics)
            else -> MainInfoOverlay.render(graphics)
        }
    }
}