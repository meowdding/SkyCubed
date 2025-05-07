package tech.thatgravyboat.skycubed.features.overlays.mining

import me.owdding.lib.builder.DisplayFactory
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.mining.GlaciteAPI
import tech.thatgravyboat.skyblockapi.api.area.mining.MiningBlock
import tech.thatgravyboat.skyblockapi.api.area.mining.MiningBlockFamily
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.level.MiningBlockMinedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.utils.CachedValue
import kotlin.time.Duration.Companion.seconds

const val PITY_AMOUNT = 2000

object PityOverlay : Overlay {
    override val name: Component = Text.of("Pity Overlay")
    override val position: Position get() = OverlayPositions.pity
    override val bounds: Pair<Int, Int> get() = display.get().getWidth() to display.get().getHeight()

    var currentPity = PITY_AMOUNT
        private set

    private val display = CachedValue(1.seconds) {
        DisplayFactory.vertical {
            string("Current Pity: $currentPity")
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        display.get().render(graphics)
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.DWARVEN_MINES)
    fun onBlockMined(event: MiningBlockMinedEvent) {
        if (!GlaciteAPI.inGlaciteTunnels()) return

        val score = PityBlock.fromBlock(event.block)?.score ?: return

        currentPity -= score.takeUnless { event.byMiningSpread } ?: (score / 2)
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.MINESHAFT)
    fun onIslandSwitch(event: IslandChangeEvent) {
        currentPity = PITY_AMOUNT
    }

    enum class PityBlock(
        val blocks: List<MiningBlock>,
        val score: Int,
    ) {
        GLACIAL_BLOCKS(MiningBlock.entries.filter { it.family in listOf(MiningBlockFamily.GEMSTONES, MiningBlockFamily.GLACITE) }, 4),
        MITHRIL_BLOCKS(MiningBlock.entries.filter { it.family == MiningBlockFamily.MITHRIL }, 2),
        TITANIUM(listOf(MiningBlock.TITANIUM), 8);

        companion object {
            fun fromBlock(block: MiningBlock) = PityBlock.entries.firstOrNull { block in it.blocks }
        }
    }

}
