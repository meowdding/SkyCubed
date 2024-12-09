package tech.thatgravyboat.skycubed.utils

import com.mojang.blaze3d.vertex.PoseStack
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.layouts.LayoutElement

operator fun <T> State<T>.getValue(thisRef: Any?, property: Any?): T = this.get()
operator fun <T> State<T>.setValue(thisRef: Any?, property: Any?, value: T) = this.set(value)

val Layout.children: List<LayoutElement>
    get() {
        val children = mutableListOf<LayoutElement>()
        this.visitChildren { children.add(it) }
        return children
    }

inline fun GuiGraphics.scissor(x: Int, y: Int, width: Int, height: Int, action: () -> Unit) {
    val xTranslation = this.pose().last().pose().m30()
    val yTranslation = this.pose().last().pose().m31()
    this.enableScissor(xTranslation.toInt() + x, yTranslation.toInt() + y, xTranslation.toInt() + x + width, yTranslation.toInt() + y + height)
    action()
    this.disableScissor()
}

inline fun GuiGraphics.pushPop(action: PoseStack.() -> Unit) {
    this.pose().pushPop(action)
}

inline fun PoseStack.pushPop(action: PoseStack.() -> Unit) {
    this.pushPose()
    this.action()
    this.popPose()
}