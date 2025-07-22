package tech.thatgravyboat.skycubed.features.overlays.map

import me.owdding.ktmodules.Module
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.displays.*
import me.owdding.lib.layouts.asWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.garden.PlotAPI
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Module
object GardenMapOverlay {

    private const val SIZE = 90
    private const val PLOT_SIZE = 18

    val canRender get() = SkyBlockIsland.GARDEN.inIsland()
    val widget: (asOverlay: Boolean) -> AbstractWidget
        get() = { asOverlay ->
            LayoutFactory.vertical {
                PlotAPI.plots.chunked(5).forEachIndexed { rowIndex, row ->
                    horizontal {
                        row.forEachIndexed { colIndex, plot ->
                            val color = if (plot.id == 0) 0xFFFFAA00u else if ((rowIndex + colIndex) % 2 == 0) 0xFF006400u else 0xFF007800u
                            val item = plot.data?.deskIcon?.let {
                                RepoItemsAPI.getItemOrNull(it) ?: BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(it)).defaultInstance
                            }
                            val background = Displays.background(color, Displays.empty(18, 18))
                            val display = listOfNotNull(
                                background,
                                item?.let { Displays.item(it).withPadding(1) },
                                plot.data?.pest?.takeIf { it.pest > 0 }?.let { pests ->
                                    Displays.text("§cൠ${pests.pest}${"+".takeIf { pests.inaccurate } ?: ""}")
                                },
                            ).asLayer()

                            if (!asOverlay) {
                                val name = "barn".takeIf { plot.isBarn } ?: plot.data?.name ?: plot.id

                                display
                                    .withTooltip(Text.of("§7Click to teleport to §a${"Plot §7- §b$name".takeUnless { plot.isBarn } ?: "The Barn"}"))
                                    .asButtonLeft {
                                        McClient.sendCommand("tptoplot $name")
                                    }
                            } else {
                                display.asWidget()
                            }.add()
                        }
                    }
                }
            }.asWidget()
        }

    fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!canRender) return

        widget(true).render(graphics, mouseX, mouseY, partialTicks)
    }

}
