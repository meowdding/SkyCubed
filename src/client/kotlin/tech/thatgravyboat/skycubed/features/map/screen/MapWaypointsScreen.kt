package tech.thatgravyboat.skycubed.features.map.screen

import com.mojang.blaze3d.platform.InputConstants
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.ui.UITexts
import earth.terrarium.olympus.client.ui.context.ContextMenu
import earth.terrarium.olympus.client.ui.modals.Modals
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.world.item.DyeColor
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.features.map.waypoints.Waypoints
import kotlin.math.roundToInt

object MapWaypointsScreen {

    private val CREATE_TITLE = Text.translatable("skycubed.map.waypoints.create.title")
    private val CREATE_PLACEHOLDER = Text.translatable("skycubed.map.waypoints.create.placeholder")
    private val CREATE_BUTTON = Text.translatable("skycubed.map.waypoints.create.button")

    private val CONTEXT_DELETE = Text.translatable("skycubed.map.waypoints.context.delete")
    private val CONTEXT_CREATE = Text.translatable("skycubed.map.waypoints.context.create")

    private fun openModal(x: Int, z: Int) {
        McClient.tell {
            val state = State.of("")
            Modals.action()
                .withTitle(CREATE_TITLE)
                .withContent(CREATE_PLACEHOLDER)
                .withContent { width -> Widgets.textInput(state).withPlaceholder("Waypoint Name").withSize(width, 20) }
                .withAction(Widgets.button()
                    .withRenderer(WidgetRenderers.text(UITexts.CANCEL))
                    .withSize(80, 24)
                    .withCallback { McScreen.self?.onClose() }
                )
                .withAction(Widgets.button()
                    .withRenderer(WidgetRenderers.text<AbstractWidget>(CREATE_BUTTON).withColor(MinecraftColors.WHITE))
                    .withSize(80, 24)
                    .withTexture(UIConstants.PRIMARY_BUTTON)
                    .withCallback {
                        Waypoints.addWaypoint(Text.of(state.get()).withColor(TextColor.BLUE), x + 0.5f, 0, z + 0.5f, DyeColor.WHITE, true)
                        McScreen.self?.onClose()
                    }
                )
                .open()
        }
    }

    fun mouseClicked(widget: MapsWidget?, mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == InputConstants.MOUSE_BUTTON_RIGHT && widget != null) {
            val poi = widget.getPoiAt(mouseX, mouseY)
            val waypoint = widget.getWaypointAt(mouseX, mouseY)
            when {
                waypoint != null -> ContextMenu.open { menu ->
                    menu.button(CONTEXT_DELETE) { Waypoints.removeWaypoint(waypoint) }
                }
                poi != null -> ContextMenu.open { menu ->
                    menu.button(CONTEXT_CREATE) {
                        Waypoints.addWaypoint(
                            poi.first.tooltip.firstOrNull() ?: Text.of("Waypoint"),
                            poi.first.position.x - 1f, poi.first.position.y, poi.first.position.z - 1f,
                            DyeColor.PURPLE
                        )
                    }
                }
                else -> ContextMenu.open { menu ->
                    menu.button(CONTEXT_CREATE) {
                        val (x, z) = widget.getWorldPosition(mouseX, mouseY)
                        openModal(x.roundToInt(), z.roundToInt())
                    }
                }
            }
            return true
        }
        return false
    }
}
