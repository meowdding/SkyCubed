package tech.thatgravyboat.skycubed.features.map.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.platform.drawTexture
import tech.thatgravyboat.skycubed.features.map.IslandData
import tech.thatgravyboat.skycubed.features.map.screen.MapShape.entries
import kotlin.math.min

enum class MapShape(
    val displayName: String,
) {
    CIRCLE("Circle"),
    SQUARE("Square"),
    ;

    fun drawMapPart(
        graphics: GuiGraphics,
        texture: ResourceLocation,
        map: IslandData,
        posX: Float,
        posY: Float,
        width: Int,
        height: Int,
        scaleX: Float,
        scaleY: Float,
        color: Int = -1,
    ) = when (this) {
        SQUARE -> graphics.drawTexture(
            texture,
            0, 0, map.width, map.height,
            color = color,
        )

        CIRCLE -> CircularMinimapRenderer.drawMapPart(
            graphics,
            texture,
            posX + width * scaleX / 2.0f + 1,
            posY + height * scaleY / 2.0f + 1,
            width * min(scaleX, scaleY) / 2.0f,
            0, 0,
            0f, 0f,
            map.width, map.height,
            map.width, map.height,
            color,
        )
    }

    override fun toString() = displayName

    val next by lazy {
        entries[(ordinal + 1) % entries.size]
    }
}
