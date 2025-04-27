package tech.thatgravyboat.skycubed.features.overlays.mining.commissions

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.displays.Displays
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionArea
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionsAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.utils.CachedValue
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures.backgroundBox
import kotlin.time.Duration.Companion.seconds

object CommissionsOverlay : Overlay {

    private val locations = setOf(
        SkyBlockIsland.DWARVEN_MINES,
        SkyBlockIsland.MINESHAFT,
        SkyBlockIsland.CRYSTAL_HOLLOWS,
    )

    private val lines = CachedValue(1.seconds) {
        val area = CommissionArea.entries.firstOrNull { it.areaCheck() } ?: return@CachedValue Displays.padding(4, Displays.text("No commissions available"))
        val commissions = CommissionsAPI.commissions.filter { it.area == area }.takeIf { it.isNotEmpty() } ?: return@CachedValue Displays.text("No commissions available")
        val lines = commissions.map { commission ->
            Displays.text(
                Text.join(
                    commission.name,
                    Text.of(": "),
                    CommissionFormatters.format(commission.name, commission.progress)
                )
            )
        }
        if (OverlaysConfig.commissions.background) {
            Displays.background(
                backgroundBox,
                Displays.padding(4, Displays.column(*lines.toTypedArray()))
            )
        } else {
            Displays.padding(4, Displays.column(*lines.toTypedArray()))
        }
    }

    override val name: Component = Text.of("Commissions")
    override val position: Position get() = OverlayPositions.commissions
    override val bounds: Pair<Int, Int> get() = lines.get().getWidth() to lines.get().getHeight()
    override val enabled: Boolean get() = OverlaysConfig.commissions.enabled && SkyBlockIsland.inAnyIsland(locations)

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.fill(0, 0, bounds.first, bounds.second, 0x50000000)
        lines.get().render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        it.button(Text.of("${if (OverlaysConfig.commissions.format) "Disable" else "Enable"} Formatting")) {
            OverlaysConfig.commissions.format = !OverlaysConfig.commissions.format
            lines.invalidate()
        }
        it.button(Text.of("${if (OverlaysConfig.commissions.background) "Disable" else "Enable"} Custom Background")) {
            OverlaysConfig.commissions.background = !OverlaysConfig.commissions.background
            lines.invalidate()
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.reset()
        }
    }
}