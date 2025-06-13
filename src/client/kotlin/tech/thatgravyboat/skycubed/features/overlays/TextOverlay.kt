package tech.thatgravyboat.skycubed.features.overlays

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.profile.StatsAPI
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.width
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.HealthDisplay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.config.overlays.TextOverlaysConfig
import kotlin.math.roundToInt

class TextOverlay(
    override val name: Component,
    override val position: Position,
    private val isEnabled: () -> Boolean,
    private val text: () -> Component
) : Overlay {

    override val bounds: Pair<Int, Int> get() = text().width to 10
    override val enabled: Boolean get() = this.isEnabled()

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.drawString(McFont.self, text(), 0, 1, 0xFFFFFF)
    }

    companion object {

        val overlays = listOf(
            TextOverlay(
                Text.of("Health"), OverlayPositions.health, { TextOverlaysConfig.healthDisplay != HealthDisplay.DISABLED },
                {
                    if (TextOverlaysConfig.healthDisplay == HealthDisplay.EFFECTIVE) {
                        val health = (StatsAPI.health * (1 + StatsAPI.defense / 100.0)).roundToInt()
                        val maxHealth = (StatsAPI.maxHealth * (1 + StatsAPI.defense / 100.0)).roundToInt()
                        Text.of("❤ $health/$maxHealth").withStyle(ChatFormatting.GREEN)
                    } else {
                        Text.of("❤ ${StatsAPI.health}/${StatsAPI.maxHealth}").withStyle(ChatFormatting.RED)
                    }
                },
            ),
            TextOverlay(
                Text.of("Mana"), OverlayPositions.mana, { TextOverlaysConfig.manaEnabled },
                {
                    Text.of("✎ ${StatsAPI.mana}/${StatsAPI.maxMana}").withStyle(ChatFormatting.AQUA)
                },
            ),
            TextOverlay(
                Text.of("Defense"), OverlayPositions.defense, { TextOverlaysConfig.defenseEnabled },
                {
                    Text.of("❈ ${StatsAPI.defense}").withStyle(ChatFormatting.GREEN)
                },
            ),
        )
    }
}
