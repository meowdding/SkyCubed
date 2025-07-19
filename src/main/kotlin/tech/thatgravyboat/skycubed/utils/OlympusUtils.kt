package tech.thatgravyboat.skycubed.utils

import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.components.events.ContainerEventHandler
import net.minecraft.client.gui.components.events.GuiEventListener

operator fun <T> State<T>.getValue(thisRef: Any?, property: Any?): T = this.get()
operator fun <T> State<T>.setValue(thisRef: Any?, property: Any?, value: T) = this.set(value)

fun ContainerEventHandler.findFocused(): GuiEventListener? {
    if (this.focused == null) return this
    if (this.focused is ContainerEventHandler) return (this.focused as ContainerEventHandler).findFocused()
    return this.focused
}
