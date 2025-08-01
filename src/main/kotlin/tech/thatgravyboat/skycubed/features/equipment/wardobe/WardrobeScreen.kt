package tech.thatgravyboat.skycubed.features.equipment.wardobe

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.constants.MinecraftColors
import earth.terrarium.olympus.client.ui.UIConstants
import me.owdding.lib.builder.LayoutBuilder
import me.owdding.lib.builder.LayoutFactory
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.asWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe.WardrobeAPI
import tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe.WardrobeSlot
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.applyBackgroundBlur
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skycubed.api.ExtraDisplays
import tech.thatgravyboat.skycubed.config.screens.WardrobeConfig
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayer
import tech.thatgravyboat.skycubed.utils.click
import java.util.concurrent.CompletableFuture

private const val BUTTON_SPACING = 3
private const val CARD_SPACING = 6
private const val ASPECT_RATIO = 3.0 / 2.0

private const val BACKGROUND_COLOR = 0xA0000000u
private const val BACKGROUND_COLOR_HOVERED = 0xD0444444u
private const val HOVER_COLOR = 0xFFAAAAAAu
private const val SELECTED_COLOR = 0xFFFFFFFFu
private const val BACKGROUND_RADIUS = 10f

private const val GO_BACK_SLOT = 48
private const val CLOSE_SLOT = 49
private const val NEXT_PAGE_SLOT = 53
private const val PREV_PAGE_SLOT = 45

object WardrobeScreen : BaseCursorScreen(CommonText.EMPTY) {

    private val TITLE by lazy {
        ExtraDisplays.background(
            BACKGROUND_COLOR, BACKGROUND_RADIUS,
            Displays.padding(
                30, 30, 8, 8,
                Displays.text(
                    Text.join(
                        Text.of("Wardrobe").withColor(TextColor.WHITE),
                        Text.of(" ʙʏ sᴋʏᴄᴜʙᴇᴅ").withColor(TextColor.GRAY),
                    ),
                ),
            ),
        ).asWidget()
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
        val uiHeight = (this.height * 0.9).toInt()
        val uiWidth = ((uiHeight * (16.0 / 9.0)).toInt()).coerceAtMost(this.width) - 90

        val displayWidth = (uiWidth / 9.0).toInt()

        val footer = LayoutFactory.horizontal(BUTTON_SPACING) {
            val menu = (screen as? AbstractContainerScreen<*>)?.menu

            createButton(UIConstants.DARK_BUTTON, "Back") {
                menu?.let { it.click(it.slots[GO_BACK_SLOT]) }
            }

            createButton(UIConstants.DANGER_BUTTON, "Close") {
                menu?.let { it.click(it.slots[CLOSE_SLOT]) }
            }

            createButton(UIConstants.PRIMARY_BUTTON, "Edit") {
                WardrobeFeature.isEditing = true
                this@WardrobeScreen.removed()
            }
        }


        val rows = LayoutFactory.vertical(CARD_SPACING) {
            WardrobeAPI.slots.chunked(9).forEachIndexed { page, slots ->
                horizontal(CARD_SPACING) {
                    slots.forEach { widget(it.getButton(displayWidth, page + 1)) }
                }
            }
        }

        FrameLayout.centerInRectangle(rows, 0, 0, this.width, this.height)
        FrameLayout.centerInRectangle(TITLE, 0, rows.y - TITLE.height - CARD_SPACING, this.width, TITLE.height)
        FrameLayout.centerInRectangle(footer, 0, rows.y + rows.height + CARD_SPACING, this.width, footer.height)

        TITLE.visitWidgets(this::addRenderableWidget)
        rows.visitWidgets(this::addRenderableWidget)
        footer.visitWidgets(this::addRenderableWidget)
    }

    private fun WardrobeSlot.getButton(displayWidth: Int, pageNumber: Int) = Widgets.button {
        it.withRenderer { graphics, context, _ ->
            val hovered = context.mouseX in context.x until context.x + context.width &&
                context.mouseY in context.y until context.y + context.height

            val entityDisplay = Displays.entity(
                DisplayEntityPlayer(CompletableFuture.completedFuture(McPlayer.skin), armor, pageNumber != currentPage),
                displayWidth, (displayWidth * ASPECT_RATIO).toInt(),
                (displayWidth / 2.0).toInt(),
                context.mouseX.toFloat() - context.x, context.mouseY.toFloat() - context.y,
            )

            if (WardrobeConfig.textured) {
                entityDisplay.render(graphics, context.x, context.y)
            } else {
                ExtraDisplays.background(
                    when {
                        hovered -> BACKGROUND_COLOR_HOVERED
                        else -> BACKGROUND_COLOR
                    },
                    BACKGROUND_RADIUS,
                    when {
                        hovered -> HOVER_COLOR
                        id == WardrobeAPI.currentSlot -> SELECTED_COLOR
                        else -> 0x0u
                    },
                    entityDisplay,
                ).render(graphics, context.x, context.y)
            }
        }
        it.withTexture(
            when {
                WardrobeConfig.textured && id == WardrobeAPI.currentSlot -> UIConstants.PRIMARY_BUTTON
                WardrobeConfig.textured && pageNumber != currentPage -> UIConstants.DARK_BUTTON
                WardrobeConfig.textured -> UIConstants.BUTTON
                else -> null
            },
        )
        it.withSize(displayWidth, (displayWidth * ASPECT_RATIO).toInt())
        it.withCallback {
            (screen as? AbstractContainerScreen<*>)?.menu?.let { menu ->
                if (pageNumber > currentPage) {
                    menu.click(menu.slots[NEXT_PAGE_SLOT])
                } else if (pageNumber < currentPage) {
                    menu.click(menu.slots[PREV_PAGE_SLOT])
                } else if (armor.none { it.isEmpty }) {
                    val index = (id - 1) % 9
                    menu.click(menu.slots[index + 36])
                }
            }
        }
    }

    override fun renderBackground(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float,
    ) {
        graphics.applyBackgroundBlur()
        this.renderTransparentBackground(graphics)
    }

    private fun LayoutBuilder.createButton(
        sprite: WidgetSprites,
        text: String,
        callback: () -> Unit,
    ) = Widgets.button()
        .withTexture(sprite)
        .withSize(60, 20)
        .withRenderer(
            WidgetRenderers.text<AbstractWidget>(Text.of(text))
                .withColor(MinecraftColors.WHITE)
                .withCenterAlignment(),
        )
        .withCallback(callback)
        .let(::widget)
}
