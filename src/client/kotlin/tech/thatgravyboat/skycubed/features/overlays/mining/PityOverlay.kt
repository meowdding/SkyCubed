package tech.thatgravyboat.skycubed.features.overlays.mining

import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Alignment
import me.owdding.lib.displays.Displays
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import tech.thatgravyboat.skyblockapi.api.area.mining.MiningBlock
import tech.thatgravyboat.skyblockapi.api.area.mining.MiningBlockFamily
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.level.MiningBlockMinedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.mineshaft.MineshaftFoundEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.hotm.HotmAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.api.overlays.RegisterOverlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.utils.CachedValue
import kotlin.math.ceil
import kotlin.time.Duration.Companion.seconds

const val PITY_AMOUNT = 2000

@Module
@RegisterOverlay
object PityOverlay : Overlay {
    override val name: Component = Text.of("Pity Overlay")
    override val position: Position get() = OverlayPositions.pity
    override val bounds: Pair<Int, Int> get() = display.get().getWidth() to display.get().getHeight()
    override val enabled: Boolean get() = inTunnels

    var currentPity = PITY_AMOUNT
        private set

    val inTunnels
        get() = SkyBlockIsland.DWARVEN_MINES.inIsland() && SkyBlockArea.inAnyArea(
            SkyBlockAreas.GLACITE_TUNNELS,
            SkyBlockAreas.GREAT_LAKE,
            SkyBlockAreas.BASECAMP,
            SkyBlockAreas.FOSSIL_RESEARCH,
        )

    private val display = CachedValue(1.seconds) {
        fun level(name: String) = HotmAPI.activePerks.entries.find { it.key == name }?.value?.level ?: 0

        val core9 = 10f.takeIf { level("Core of the Mountain") >= 9 } ?: 0f
        val anomalousDesire = (level("Anomalous Desire").times(10).plus(20)).toFloat()
        val surveyor = level("Surveyor").times(0.75).toFloat()
        val odds = (100 + core9 + anomalousDesire + surveyor) / (currentPity)

        DisplayFactory.vertical {
            string("Current Pity: $currentPity")
            PityBlock.entries.reversed().forEach { entry ->
                val leftToMine = ceil(currentPity / entry.score.toDouble())
                horizontal(alignment = Alignment.CENTER) {
                    entry.icons.forEach { display(Displays.item(it)) }
                    string(": ${leftToMine.toFormattedString()}")
                }
            }

            string("Odds: ${odds.toFormattedString()}% (1 in ${ceil(100 / odds).toInt()})")
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        display.get().render(graphics)
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.DWARVEN_MINES)
    fun onBlockMined(event: MiningBlockMinedEvent) {
        if (!inTunnels) return

        val score = PityBlock.fromBlock(event.block)?.score ?: return

        currentPity -= score.takeUnless { event.byMiningSpread } ?: (score / 2)
    }

    @Subscription
    fun onIslandSwitch(event: MineshaftFoundEvent) {
        currentPity = PITY_AMOUNT
    }

    enum class PityBlock(
        val blocks: List<MiningBlock>,
        val score: Int,
        val icons: List<Block> = blocks.flatMap { it.blocks },
    ) {
        TITANIUM(listOf(MiningBlock.TITANIUM), 8),
        GLACIAL_BLOCKS(
            MiningBlock.entries.filter { it.family in listOf(MiningBlockFamily.GEMSTONES, MiningBlockFamily.GLACITE) },
            4,
            listOf(Blocks.RED_STAINED_GLASS, Blocks.SMOOTH_RED_SANDSTONE, Blocks.CLAY, Blocks.PACKED_ICE),
        ),
        MITHRIL_BLOCKS(
            MiningBlock.entries.filter { it.family == MiningBlockFamily.MITHRIL },
            2,
            listOf(Blocks.LIGHT_BLUE_WOOL),
        ),
        ;

        operator fun component1() = blocks
        operator fun component2() = score

        companion object {
            fun fromBlock(block: MiningBlock) = PityBlock.entries.firstOrNull { block in it.blocks }
        }
    }

}
