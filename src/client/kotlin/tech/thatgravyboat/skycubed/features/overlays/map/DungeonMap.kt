package tech.thatgravyboat.skycubed.features.overlays.map

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.Mth
import tech.thatgravyboat.skycubed.features.dungeonmap.DungeonFeatures
import tech.thatgravyboat.skycubed.features.dungeonmap.position.RenderPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.combinedSize

object DungeonMap {

    val canRender: Boolean get() = true

    fun render(graphics: GuiGraphics) {
        val instance = DungeonFeatures.currentInstance ?: return
        val dungeonMap = instance.map ?: return

        dungeonMap.roomMap.forEachIndexed row@{ x, column ->
            column.forEachIndexed column@{ y, value ->
                graphics.fill(
                    ((combinedSize + 2) * x),
                    ((combinedSize + 2) * y),
                    ((combinedSize + 2) * x) + combinedSize,
                    ((combinedSize + 2) * y) + combinedSize,
                    0xFF000000u.toInt().or(value?.hashCode() ?: -1)
                )
            }
        }

        instance.players.filterNotNull().forEach { player ->
            val renderPosition = player.position.convertTo<RenderPosition>()
            graphics.fill(
                renderPosition.x,
                renderPosition.y,
                renderPosition.x + 10,
                renderPosition.y + 10,
                if (player.isSelf) -1 else player.hashCode().or(0xFF000000u.toInt())
            )
        }
    }

    private fun getColorComponent(value: Int, multiplier: Double): Float {
        return Mth.frac((multiplier.toFloat() * value.toFloat())) * 0.9f + 0.1f
    }

}