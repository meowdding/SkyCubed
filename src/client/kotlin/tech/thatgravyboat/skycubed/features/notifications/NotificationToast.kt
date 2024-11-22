package tech.thatgravyboat.skycubed.features.notifications

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastManager
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.utils.font

data class NotificationToast(
    private var text: List<FormattedCharSequence>,
    val id: String?,
    private val displayTime: Int
) : Toast {

    private var removalTime: Long = -1
    private var replaced = false

    override fun width(): Int = 160
    override fun height(): Int = (7 + text.size * 10).coerceAtLeast(32)

    override fun getToken(): Any = this.id ?: Toast.NO_TOKEN

    override fun render(graphics: GuiGraphics, font: Font, ignored2: Long) {
        render(graphics)
    }

    override fun getWantedVisibility(): Toast.Visibility {
        return if (this.removalTime <= System.currentTimeMillis()) Toast.Visibility.HIDE else Toast.Visibility.SHOW
    }

    override fun update(toastManager: ToastManager, l: Long) {

    }

    fun render(graphics: GuiGraphics) {
        if (this.removalTime == -1L || this.replaced) {
            this.removalTime = System.currentTimeMillis() + this.displayTime
            this.replaced = false
        }
        graphics.blitSprite(RenderType::guiTextured, BACKGROUND, 0, 0, this.width(), this.height())

        val y = 1 + this.height() / 2 - text.size * 5
        val x = 4

        for (line in text.indices) {
            graphics.drawString(graphics.font, text[line], x, y + line * 10, -1, false)
        }
    }

    fun replace(text: List<FormattedCharSequence>) {
        this.text = text
        this.replaced = true
    }

    companion object {

        private val BACKGROUND = SkyCubed.id("notification")

        private val notifications = mutableListOf<NotificationToast>()

        fun add(id: String?, text: Component, time: Int) {
            val component = McClient.toasts
            val font = component.minecraft.font
            val list = font.split(text, 152)
            if (id != null) {
                val existing = component.getToast(NotificationToast::class.java, id)
                if (existing != null) {
                    existing.replace(list)
                    return
                }
            }
            val toast = NotificationToast(list, id, time)
            component.addToast(toast)
            this.notifications.add(toast)
            if (this.notifications.size > 50) {
                this.notifications.removeFirst()
            }
        }

        fun notifications(): List<NotificationToast> = this.notifications
    }
}