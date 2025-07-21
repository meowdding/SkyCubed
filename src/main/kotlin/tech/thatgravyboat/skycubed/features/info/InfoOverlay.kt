package tech.thatgravyboat.skycubed.features.info

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.ktmodules.AutoCollect
import me.owdding.skycubed.generated.SkyCubedRegisteredInfos
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.drawSprite
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.overlays.EditableProperty
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.api.overlays.RegisterOverlay
import tech.thatgravyboat.skycubed.config.overlays.InfoHudOverlayConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.features.overlays.vanilla.barDisabled
import tech.thatgravyboat.skycubed.features.overlays.vanilla.disabled
import tech.thatgravyboat.skycubed.mixins.BossHealthOverlayAccessor

@AutoCollect("RegisteredInfos")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class RegisterInfoOverlay

@RegisterOverlay
object InfoOverlay : Overlay {

    private val infoOverlays = mutableListOf<InfoProvider>()

    init {
        SkyCubedRegisteredInfos.collected.forEach { overlay ->
            infoOverlays.add(overlay)
        }
    }

    override val name: Component = Component.literal("Info Overlay")
    override val position: Position = Position()
        get() {
            val bossEvents = (McClient.gui.bossOverlay as? BossHealthOverlayAccessor)?.events
            val modifier: Int = bossEvents.let { events ->
                var modifier = 0
                events?.forEach { event ->
                    if (event.value.disabled) {
                        modifier -= 19
                    } else if (event.value.barDisabled) {
                        modifier -= 5
                    }
                }
                modifier
            }
            val bossOverlayY = bossEvents
                ?.size
                ?.takeIf { it > 0 }
                ?.let { 17 + (it - 1) * 19 + modifier } ?: 0

            val (_, y) = OverlayPositions.info

            field.scale = OverlayPositions.info.scale
            field.y = if (y >= bossOverlayY) y else bossOverlayY
            field.x = (McClient.window.guiScaledWidth - (34 * field.scale).toInt()) / 2

            return field
        }
    override val enabled: Boolean get() = InfoHudOverlayConfig.enabled
    override val properties: Collection<EditableProperty> = setOf(EditableProperty.Y, EditableProperty.SCALE, EditableProperty.MISC)
    override val bounds: Pair<Int, Int> = 34 to 34

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.drawSprite(CommonInfoDisplays.BASE, 0, 0, 34, 34)
        CommonInfoDisplays.baseDisplay.render(graphics, 0, 0)

        infoOverlays.groupBy { it.location }.forEach { (location, overlays) ->
            val (xOffset, horizontalAlignment) = when (location) {
                InfoLocation.TOP_LEFT, InfoLocation.BOTTOM_LEFT -> 0 to 1f
                InfoLocation.TOP_RIGHT, InfoLocation.BOTTOM_RIGHT -> 34 to 0f
            }
            val yOffset = when (location) {
                InfoLocation.TOP_LEFT, InfoLocation.TOP_RIGHT -> 2
                InfoLocation.BOTTOM_LEFT, InfoLocation.BOTTOM_RIGHT -> 18
            }
            overlays.filter { it.shouldDisplay() }.forEachIndexed { index, overlay ->
                location.withBackground(overlay.getDisplay()).render(graphics, xOffset, yOffset, horizontalAlignment)
            }
        }
    }

    override fun onRightClick() = ContextMenu.open {
        it.dangerButton(Text.of("Reset Position")) {
            OverlayPositions.info.reset()
        }
    }

    override fun setY(y: Int) {
        val height = McClient.window.guiScaledHeight
        if (bounds.second == 0 || bounds.second >= height) return
        OverlayPositions.info.y = if (y < height / 2) y.coerceAtLeast(0) else (y - height).coerceAtMost(-bounds.second)
    }

    override fun setScale(scale: Float) {
        OverlayPositions.info.scale = (scale * 100f).toInt() / 100f
    }
}
