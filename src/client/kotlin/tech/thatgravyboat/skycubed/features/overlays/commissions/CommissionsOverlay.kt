package tech.thatgravyboat.skycubed.features.overlays.commissions

import earth.terrarium.olympus.client.ui.context.ContextMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionArea
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionsAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.utils.CachedValue
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
        Displays.padding(4, Displays.column(*lines.toTypedArray()))
    }

    override val name: Component = Text.of("Commissions")
    override val position: Position get() = OverlaysConfig.commissions
    override val bounds: Pair<Int, Int> get() = lines.get().getWidth() to lines.get().getHeight()
    override val enabled: Boolean get() = OverlaysConfig.commissionsEnabled

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        if (!SkyBlockIsland.inAnyIsland(locations)) return

        graphics.fill(0, 0, bounds.first, bounds.second, 0x50000000)
        lines.get().render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        it.button(Text.of("${if (OverlaysConfig.commissionsFormat) "Disable" else "Enable"} Formatting")) {
            OverlaysConfig.commissionsFormat = !OverlaysConfig.commissionsFormat
            lines.invalidate()
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.reset()
        }
    }
}