package tech.thatgravyboat.skycubed.features.map.dev

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.ui.UIIcons
import earth.terrarium.olympus.client.utils.ListenableState
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.builder.MIDDLE
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.api.conditions.Condition
import tech.thatgravyboat.skycubed.features.map.dev.MapEditor.posAsVec3i
import tech.thatgravyboat.skycubed.features.map.pois.ConditionalPoi
import tech.thatgravyboat.skycubed.features.map.pois.NpcPoi
import tech.thatgravyboat.skycubed.features.map.pois.Poi
import tech.thatgravyboat.skycubed.features.map.pois.PortalPoi
import tech.thatgravyboat.skycubed.utils.SkyCubedScreen

class MapPoiEditScreen(poi: Poi, val list: MutableList<Poi>, val parent: Screen? = null, val entity: Entity? = null) : SkyCubedScreen("map_poi") {

    val poi: Poi
    val actualPoi: Poi

    init {
        var tempPoi = poi
        while (tempPoi is ConditionalPoi) tempPoi = tempPoi.poi

        this.poi = tempPoi
        this.actualPoi = poi
    }

    private val xState = ListenableState.of(poi.position.x.toString())
    private val yState = ListenableState.of(poi.position.y.toString())
    private val zState = ListenableState.of(poi.position.z.toString())

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
                            xState, width = 100,
                            onChange = {
                                poi.position.x = it.toIntValue()
                            },
                        )
                    }
                    horizontal(alignment = MIDDLE) {
                        string("y")
                        this.textInput(
                            yState, width = 100,
                            onChange = {
                                poi.position.y = it.toIntValue()
                            },
                        )
                    }
                    horizontal(alignment = MIDDLE) {
                        string("z")
                        this.textInput(
                            zState, width = 100,
                            onChange = {
                                poi.position.z = it.toIntValue()
                            },
                        )
                    }

                    val entity = entity ?: return@horizontal
                    val resyncButton = Widgets.button().apply {
                        withSize(20)
                        withRenderer(WidgetRenderers.icon(UIIcons.LINK))
                        withCallback {
                            poi.position = entity.posAsVec3i()
                            xState.set(poi.position.x.toString())
                            yState.set(poi.position.y.toString())
                            zState.set(poi.position.z.toString())
                        }
                    }
                    widget(resyncButton)
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
            when (actualPoi) {
                is NpcPoi -> {
                    widget(
                        Widgets.button()
                            .withSize(100, 20)
                            .withRenderer(WidgetRenderers.text(Text.of("Convert to conditional")))
                            .withCallback {
                                list.remove(actualPoi)

                                val newPoi = ConditionalPoi(Condition.TRUE, Condition.FALSE, actualPoi)
                                list.add(newPoi)
                                McClient.setScreenAsync { MapPoiEditScreen(newPoi, list, parent, entity) }
                            },
                    )
                }

                is ConditionalPoi -> {
                    widget(
                        Widgets.button()
                            .withSize(100, 20)
                            .withRenderer(WidgetRenderers.text(Text.of("Convert to unconditional")))
                            .withCallback {
                                list.remove(actualPoi)

                                list.add(poi)
                                McClient.setScreenAsync { MapPoiEditScreen(poi, list, parent, entity) }
                            },
                    )
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

//     override fun renderBlurredBackground() {}

    override fun onClose() {
        parent?.let { McClient.setScreenAsync { it } }
        super.onClose()
    }
}
