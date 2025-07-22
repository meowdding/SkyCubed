package tech.thatgravyboat.skycubed.features.map.screen

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import me.owdding.lib.displays.asWidget
import me.owdding.lib.layouts.ScalableWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.FrameLayout
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.api.ExtraDisplays
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.utils.ResettingState
import tech.thatgravyboat.skycubed.utils.getValue
import tech.thatgravyboat.skycubed.utils.setValue

class DynamicMapRenderer(
    val map: String?,
    scale: ResettingState<Float>,

    xOffset: ResettingState<Double>,
    yOffset: ResettingState<Double>,

    width: Int,
    height: Int,
) : BaseParentWidget(width, height) {
    val widget = ScalableWidget(map?.let { Maps.getDynamicWidget(it) } ?: ExtraDisplays.missingTextureDisplay(width, height).asWidget())

    init {
        addRenderableWidget(widget)
    }

    private var scale by scale

    override fun renderWidget(p0: GuiGraphics, p1: Int, p2: Int, p3: Float) {
        widget.scale(scale.toDouble())
        FrameLayout.centerInRectangle(widget, 0, 0, McClient.window.width / 2, McClient.window.height / 2)
        widget.render(p0, p1, p2, p3)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        scale += (scrollY / 5).toFloat()
        scale = scale.coerceAtLeast(0.5f).coerceAtMost(5f)

        return true
    }

}
