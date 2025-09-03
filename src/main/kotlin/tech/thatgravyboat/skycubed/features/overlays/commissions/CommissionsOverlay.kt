package tech.thatgravyboat.skycubed.features.overlays.commissions

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.displays.Displays
import me.owdding.lib.overlays.ConfigPosition
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionArea
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionsAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.config.overlays.CommissionOverlayConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions

import tech.thatgravyboat.skycubed.utils.CachedValue
import tech.thatgravyboat.skycubed.utils.RegisterOverlay
import tech.thatgravyboat.skycubed.utils.SkyCubedOverlay
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures.backgroundBox
import tech.thatgravyboat.skycubed.utils.invalidateCache
import kotlin.time.Duration.Companion.seconds

@RegisterOverlay
object CommissionsOverlay : SkyCubedOverlay {

    private val locations = setOf(
        SkyBlockIsland.DWARVEN_MINES,
        SkyBlockIsland.MINESHAFT,
        SkyBlockIsland.CRYSTAL_HOLLOWS,
    )

    private val lines by CachedValue(1.seconds) {
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
        if (CommissionOverlayConfig.background) {
            Displays.background(
                backgroundBox,
                Displays.padding(4, Displays.column(*lines.toTypedArray()))
            )
        } else {
            Displays.padding(4, Displays.column(*lines.toTypedArray()))
        }
    }

    override val name: Component = Text.of("Commissions")
    override val position: ConfigPosition get() = OverlayPositions.commissions
    override val bounds: Pair<Int, Int> get() = lines.getWidth() to lines.getHeight()
    override val enabled: Boolean get() = CommissionOverlayConfig.enabled && SkyBlockIsland.inAnyIsland(locations)

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.fill(0, 0, bounds.first, bounds.second, 0x50000000)
        lines.render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        it.button(Text.of("${if (CommissionOverlayConfig.format) "Disable" else "Enable"} Formatting")) {
            CommissionOverlayConfig.format = !CommissionOverlayConfig.format
            this::lines.invalidateCache()
        }
        it.button(Text.of("${if (CommissionOverlayConfig.background) "Disable" else "Enable"} Custom Background")) {
            CommissionOverlayConfig.background = !CommissionOverlayConfig.background
            this::lines.invalidateCache()
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.resetPosition()
        }
    }
}
