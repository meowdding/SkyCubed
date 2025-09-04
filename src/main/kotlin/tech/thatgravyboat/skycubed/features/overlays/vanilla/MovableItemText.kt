package tech.thatgravyboat.skycubed.features.overlays.vanilla

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.displays.Alignment
import me.owdding.lib.overlays.ConfigPosition
import me.owdding.lib.overlays.EditableProperty
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.config.overlays.ItemTextOverlayConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.features.overlays.map.MinimapOverlay
import tech.thatgravyboat.skycubed.mixins.GuiAccessor
import tech.thatgravyboat.skycubed.utils.RegisterOverlay
import tech.thatgravyboat.skycubed.utils.SkyCubedOverlay

@RegisterOverlay
object MovableItemText : SkyCubedOverlay {

    const val WIDTH = 182

    override val name: Component = Text.of("Moveable Item Text")
    override val enabled: Boolean get() = ItemTextOverlayConfig.moveable && LocationAPI.isOnSkyBlock
    override val properties: Collection<EditableProperty> = setOf(EditableProperty.X, EditableProperty.Y)
    override val position: ConfigPosition = OverlayPositions.itemtext
    override val bounds: Pair<Int, Int> = WIDTH to 11

    /**
     * Handling happens in [tech.thatgravyboat.skycubed.mixins.GuiMixin]
     */
    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        if (!this.isEditing()) return
        val gui = McClient.self.gui ?: return
        val accessor = gui as? GuiAccessor ?: return
        accessor.setToolHighlightTimer(20)
    }

    override fun onRightClick() = ContextMenu.open {
        val alignment = Alignment.entries[(ItemTextOverlayConfig.alignment.ordinal + 1) % Alignment.entries.size]
        it.button(Text.of("Render at ${alignment.toFormattedName()}")) {
            ItemTextOverlayConfig.alignment = alignment
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            MinimapOverlay.position.resetPosition()
        }
    }
}
