package tech.thatgravyboat.skycubed.features.overlays.commissions

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.builder.DisplayFactory
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
import tech.thatgravyboat.skycubed.utils.OverlayBackgroundConfig
import tech.thatgravyboat.skycubed.utils.RegisterOverlay
import tech.thatgravyboat.skycubed.utils.SkyCubedOverlay
import tech.thatgravyboat.skycubed.utils.invalidateCache
import tech.thatgravyboat.skycubed.utils.next
import kotlin.time.Duration.Companion.seconds

@RegisterOverlay
object CommissionsOverlay : SkyCubedOverlay {

    private val locations = setOf(
        SkyBlockIsland.DWARVEN_MINES,
        SkyBlockIsland.MINESHAFT,
        SkyBlockIsland.CRYSTAL_HOLLOWS,
    )

    private val lines by CachedValue(1.seconds) {
        DisplayFactory.vertical {
            val area = CommissionArea.entries.firstOrNull { it.areaCheck() } ?: run {
                string("No commissions available")
                return@vertical
            }
            val commissions = CommissionsAPI.commissions.filter { it.area == area }.takeIf { it.isNotEmpty() } ?: run {
                string("No commissions available")
                return@vertical
            }
            commissions.forEach { commission ->
                string(Text.join("${commission.name}: ", CommissionFormatters.format(commission.name, commission.progress)))
            }
        }
    }

    override val name: Component = Text.of("Commissions")
    override val position: ConfigPosition get() = OverlayPositions.commissions
    override val actualBounds: Pair<Int, Int> get() = lines.getWidth() to lines.getHeight()
    override val enabled: Boolean get() = CommissionOverlayConfig.enabled && SkyBlockIsland.inAnyIsland(locations)
    override val background: OverlayBackgroundConfig get() = CommissionOverlayConfig.background

    override fun renderWithBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        lines.render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        it.button(Text.of("${if (CommissionOverlayConfig.format) "Disable" else "Enable"} Formatting")) {
            CommissionOverlayConfig.format = !CommissionOverlayConfig.format
            this::lines.invalidateCache()
        }
        val text = when (CommissionOverlayConfig.background) {
            OverlayBackgroundConfig.TEXTURED -> "Textured Background"
            OverlayBackgroundConfig.TRANSLUCENT -> "Translucent Background"
            OverlayBackgroundConfig.NO_BACKGROUND -> "No Background"
        }
        it.button(Text.of(text)) {
            CommissionOverlayConfig.background = CommissionOverlayConfig.background.next()
            this::lines.invalidateCache()
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.resetPosition()
        }
    }
}
