package tech.thatgravyboat.skycubed.features.notifications

import com.teamresourceful.resourcefullib.common.utils.TriState
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.dropdown.DropdownState
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.ui.Overlay
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.ui.UIIcons
import earth.terrarium.olympus.client.ui.UITexts
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component
import org.apache.commons.lang3.function.Consumers
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.platform.drawSprite
import tech.thatgravyboat.skyblockapi.platform.pushPop
import tech.thatgravyboat.skyblockapi.platform.translate

private const val PADDING = 10
private const val WIDTH = 170
private const val FULL_WIDTH = WIDTH + PADDING * 2

class NotificationsScreen : Overlay(McScreen.self) {

    private val category: DropdownState<String?> = DropdownState.empty()

    override fun renderBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks)
        this.renderTransparentBackground(graphics)
        graphics.drawSprite(
            UIConstants.MODAL,
            this.width - WIDTH - PADDING * 2,
            0,
            WIDTH + PADDING * 2,
            this.height,
        )
    }

    override fun init() {
        val notifications = NotificationToast.notifications()
        val ids = notifications.map { it.id }.distinct()

        val x = this.width - FULL_WIDTH
        val y = 0

        Layouts.column()
            .withGap(PADDING)
            .withPosition(x + PADDING, y + PADDING)
            .withChild(
                Layouts.row()
                    .withPosition(x + PADDING, y + PADDING)
                    .withChild(
                        StringWidget(
                            WIDTH - 24, 24,
                            Component.translatable("skycubed.notifications"),
                            McFont.self
                        ).alignLeft()
                    )
                    .withChild(Widgets.button {
                        it.withSize(24, 24)
                        it.withRenderer(WidgetRenderers.icon<Button?>(UIIcons.CROSS).withCentered(12, 12))
                        it.withTooltip(UITexts.BACK)
                        it.withCallback { this.onClose() }
                    })
            )
            .withChild(
                Widgets.dropdown(
                    category,
                    listOf(null) + ids,
                    {
                        Component.translatable("skycubed.notification.type.${it ?: "all"}")
                    },
                    {
                        it.withSize(WIDTH, 24)
                        if (notifications.isEmpty()) it.asDisabled()
                    },
                    Consumers.nop()
                )
            )
            .withChild(
                Widgets.list { list ->
                    list.setSize(WIDTH, this.height - PADDING * 4 - 24 * 2)
                    list.withScrollableY(TriState.TRUE)

                    list.withContents { contents ->
                        contents.withGap(1)

                        notifications
                            .filter { category.get() == null || it.id == category.get() }
                            .reversed()
                            .forEach { toast ->
                                contents.withChild(
                                    Widgets.button {
                                        it.withTexture(null)
                                        it.withSize(toast.width(), toast.height())
                                        it.withRenderer { graphics, context, _ ->
                                            graphics.pushPop {
                                                graphics.translate(context.x.toDouble(), context.y.toDouble())
                                                toast.render(graphics)
                                            }
                                        }
                                    },
                                )
                            }
                    }
                },
            )
            .build(::addRenderableWidget)
    }
}
