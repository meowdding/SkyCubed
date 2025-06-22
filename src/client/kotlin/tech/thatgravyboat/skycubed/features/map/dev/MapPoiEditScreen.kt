package tech.thatgravyboat.skycubed.features.map.dev

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.utils.ListenableState
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.builder.MIDDLE
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.features.map.pois.NpcPoi
import tech.thatgravyboat.skycubed.features.map.pois.Poi
import tech.thatgravyboat.skycubed.features.map.pois.PortalPoi
import tech.thatgravyboat.skycubed.utils.SkyCubedScreen

class MapPoiEditScreen(val poi: Poi, val parent: Screen? = null) : SkyCubedScreen("map_poi") {
    override fun init() {
        LayoutFactory.vertical(5) {
            horizontal(2, alignment = MIDDLE) {
                string("type:")
                string(poi.id)
            }
            vertical {
                string("tooltip")
                val tooltip = if (poi is NpcPoi) {
                    poi.stringTooltip.joinToString("\n")
                } else {
                    poi.tooltip.joinToString("\n") { it.string }
                }

                val tooltipSetter: (String) -> Unit = if (poi is NpcPoi) {
                    { lines ->
                        poi.stringTooltip.clear()
                        poi.stringTooltip.addAll(lines.split("\n"))
                    }
                } else {
                    { lines ->
                        poi.tooltip.clear()
                        poi.tooltip.addAll(lines.split("\n").map { Text.of(it) })
                    }
                }

                Widgets.multilineTextInput(
                    ListenableState.of(tooltip).also { it.registerListener(tooltipSetter) },
                ) { widget ->
                    widget.withSize(200, 100)
                }.add()
            }
            vertical {
                string("Position: ")
                horizontal(5) {
                    horizontal(alignment = MIDDLE) {
                        string("x")
                        this.textInput(
                            ListenableState.of(poi.position.x.toString()), width = 100,
                            onChange = {
                                poi.position.x = it.toIntValue()
                            },
                        )
                    }
                    horizontal(alignment = MIDDLE) {
                        string("y")
                        this.textInput(
                            ListenableState.of(poi.position.y.toString()), width = 100,
                            onChange = {
                                poi.position.y = it.toIntValue()
                            },
                        )
                    }
                    horizontal(alignment = MIDDLE) {
                        string("z")
                        this.textInput(
                            ListenableState.of(poi.position.z.toString()), width = 100,
                            onChange = {
                                poi.position.z = it.toIntValue()
                            },
                        )
                    }
                }
            }
            when (poi) {
                is NpcPoi -> {
                    horizontal(alignment = MIDDLE) {
                        string("Texture")
                        textInput(ListenableState.of(poi.texture), width = 200, onChange = { poi.texture = it })
                    }
                    horizontal(alignment = MIDDLE) {
                        string("Link")
                        textInput(ListenableState.of(poi.actualLink), width = 200, onChange = { poi.actualLink = it })
                    }
                    horizontal(alignment = MIDDLE) {
                        string("Name")
                        Widgets.multilineTextInput(
                            ListenableState.of(poi.name).also { it.registerListener { poi.name = it.trim() } },
                        ) { widget ->
                            widget.withSize(100, 20)
                        }.apply {
                            setInitialFocus(this)
                        }.add()
                    }
                }

                is PortalPoi -> {
                    horizontal(alignment = MIDDLE) {
                        string("Warp")
                        textInput(ListenableState.of(poi.destination), width = 200, onChange = { poi.destination = it })
                    }
                }
            }
        }.also {
            Displays.sprite(olympus("buttons/normal"), it.width + 20, (it.height + 20).coerceAtMost(height)).asWidget()
                .center().applyAsRenderable()
        }.asScrollable(width, height).center().applyLayout()


    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, f: Float) {
        parent?.render(graphics, -1, -1, f)
        super.render(graphics, mouseX, mouseY, f)
    }

    override fun onClose() {
        parent?.let { McClient.setScreenAsync { it } }
        super.onClose()
    }
}
