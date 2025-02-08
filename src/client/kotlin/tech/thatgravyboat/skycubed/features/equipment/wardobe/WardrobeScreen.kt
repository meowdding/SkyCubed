package tech.thatgravyboat.skycubed.features.equipment.wardobe

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.ui.UIConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import tech.thatgravyboat.skyblockapi.api.profile.wardrobe.WardrobeAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.api.displays.Displays
import tech.thatgravyboat.skycubed.api.displays.asWidget
import tech.thatgravyboat.skycubed.config.screens.ScreensConfig
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayer
import tech.thatgravyboat.skycubed.utils.click

private const val CARD_SPACING = 6
private const val ASPECT_RATIO = 3.0 / 2.0

private const val BACKGROUND_COLOR = 0xA0000000u
private const val HOVER_COLOR = 0xFFAAAAAAu
private const val SELECTED_COLOR = 0xFFFFFFFFu
private const val BACKGROUND_RADIUS = 10f

private const val NEXT_PAGE_SLOT = 53
private const val PREV_PAGE_SLOT = 45

object WardrobeScreen : BaseCursorScreen(CommonText.EMPTY) {

    private val TITLE by lazy {
        Displays.background(BACKGROUND_COLOR, BACKGROUND_RADIUS, Displays.padding(
            30, 30, 8, 8,
            Displays.text(Text.join(
                Text.of("Wardrobe").withColor(TextColor.WHITE),
                Text.of(" ʙʏ sᴋʏᴄᴜʙᴇᴅ").withColor(TextColor.GRAY)
            ))
        ))
    }

    var screen: Screen? = null
        set(value) {
            if (field != value) {
                field = value
                if (value == null) {
                    removed()
                }
            }
        }

    var currentPage = 0

    override fun init() {
        val displayWidth = ((this.width - 90) / 9.0).toInt()

        val header = TITLE.asWidget()
        val footer = Widgets.button()
            .withTexture(UIConstants.DARK_BUTTON)
            .withSize(60, 20)
            .withRenderer(WidgetRenderers.text<AbstractWidget>(Text.of("Edit"))
                .withColor(MinecraftColors.WHITE)
                .withCenterAlignment()
            )
            .withCallback {
                WardrobeFeature.isEditing = true
                this.removed()
            }

        val rows = LinearLayout.vertical().spacing(CARD_SPACING)

        for ((page, slots) in WardrobeAPI.slots.chunked(9).withIndex()) {
            val pageNumber = page + 1
            val row = LinearLayout.horizontal().spacing(CARD_SPACING)

            for (slot in slots) {
                val empty = slot.armor.all { it.isEmpty }

                row.addChild(Widgets.button {
                    it.withRenderer { graphics, context, _ ->
                        val hovered = context.mouseX in context.x until context.x + context.width &&
                                context.mouseY in context.y until context.y + context.height

                        val entityDisplay = Displays.entity(
                            DisplayEntityPlayer(McPlayer.skin, slot.armor, pageNumber != currentPage),
                            displayWidth, (displayWidth * ASPECT_RATIO).toInt(),
                            (displayWidth / 2.0).toInt(),
                            context.mouseX.toFloat() - context.x, context.mouseY.toFloat() - context.y
                        )

                        if (ScreensConfig.wardrobe.textured) {
                            entityDisplay.render(graphics, context.x, context.y)
                        } else {
                            Displays.background(
                                BACKGROUND_COLOR,
                                BACKGROUND_RADIUS,
                                when {
                                    hovered -> HOVER_COLOR
                                    slot.id == WardrobeAPI.currentSlot -> SELECTED_COLOR
                                    else -> 0x0u
                                },
                                entityDisplay
                            ).render(graphics, context.x, context.y)
                        }
                    }
                    it.withTexture(when {
                        ScreensConfig.wardrobe.textured && slot.id == WardrobeAPI.currentSlot -> UIConstants.PRIMARY_BUTTON
                        ScreensConfig.wardrobe.textured && pageNumber != currentPage -> UIConstants.DARK_BUTTON
                        ScreensConfig.wardrobe.textured -> UIConstants.BUTTON
                        else -> null
                    })
                    it.withSize(displayWidth, (displayWidth * ASPECT_RATIO).toInt())
                    it.withCallback {
                        (screen as? AbstractContainerScreen<*>)?.menu?.let { menu ->
                            if (pageNumber > currentPage) {
                                menu.click(menu.slots[NEXT_PAGE_SLOT])
                            } else if (pageNumber < currentPage) {
                                menu.click(menu.slots[PREV_PAGE_SLOT])
                            } else if (!empty) {
                                val index = (slot.id - 1) % 9
                                menu.click(menu.slots[index + 36])
                            }
                        }
                    }
                })
            }

            rows.addChild(row)
        }

        rows.arrangeElements()
        FrameLayout.centerInRectangle(rows, 0, 0, this.width, this.height)
        FrameLayout.centerInRectangle(header, 0, rows.y - header.height - CARD_SPACING, this.width, header.height)
        FrameLayout.centerInRectangle(footer, 0, rows.y + rows.height + CARD_SPACING, this.width, footer.height)

        header.visitWidgets(this::addRenderableWidget)
        rows.visitWidgets(this::addRenderableWidget)
        footer.visitWidgets(this::addRenderableWidget)
    }

    override fun renderBackground(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float
    ) {
        this.renderBlurredBackground()
        this.renderTransparentBackground(graphics)
    }
}