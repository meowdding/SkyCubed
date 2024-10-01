package tech.thatgravyboat.skycubed.features.notifications

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastComponent
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.utils.font

data class NotificationToast(
    private var text: List<FormattedCharSequence>,
    private val id: String?,
    private val displayTime: Int
) : Toast {

    private var removalTime: Long = -1
    private var replaced = false

    override fun width(): Int = 160
    override fun height(): Int = (7 + text.size * 10).coerceAtLeast(32)
    override fun getToken(): Any = this.id ?: Toast.NO_TOKEN

    override fun render(graphics: GuiGraphics, toastComponent: ToastComponent, l: Long): Toast.Visibility {
        if (this.removalTime == -1L || this.replaced) {
            this.removalTime = System.currentTimeMillis() + this.displayTime
            this.replaced = false
        }
        graphics.blitSprite(BACKGROUND, 0, 0, this.width(), this.height())

        val y = 1 + this.height() / 2 - text.size * 5
        val x = 4

        for (line in text.indices) {
            graphics.drawString(graphics.font, text[line], x, y + line * 10, -1, false)
        }

        return if (this.removalTime <= System.currentTimeMillis()) Toast.Visibility.HIDE else Toast.Visibility.SHOW
    }

    fun replace(text: List<FormattedCharSequence>) {
        this.text = text
        this.replaced = true
    }

    companion object {

        private val BACKGROUND = SkyCubed.id("notification")

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
            component.addToast(NotificationToast(list, id, time))
        }
    }
}