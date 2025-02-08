package tech.thatgravyboat.skycubed.api.displays

import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.buttons.Button
import net.minecraft.network.chat.Component


fun List<Any>.toColumn(spacing: Int = 0, horizontalAlignment: Alignment = Alignment.START): Display {
    return Displays.column(
        *this.map {
            when (it) {
                is String -> Displays.text(it)
                is Component -> Displays.text(it)
                is Display -> it
                else -> throw IllegalArgumentException("Unsupported type: ${it::class.simpleName}")
            }
        }.toTypedArray(),
        spacing = spacing,
        horizontalAlignment = horizontalAlignment,
    )
}

fun List<Any>.toRow(spacing: Int = 0): Display {
    return Displays.row(
        *this.map {
            when (it) {
                is String -> Displays.text(it)
                is Component -> Displays.text(it)
                is Display -> it
                else -> throw IllegalArgumentException("Unsupported type: ${it::class.simpleName}")
            }
        }.toTypedArray(),
        spacing = spacing
    )
}

fun List<Any>.asLayer(): Display {
    return Displays.layered(
        *this.map {
            when (it) {
                is String -> Displays.text(it)
                is Component -> Displays.text(it)
                is Display -> it
                else -> throw IllegalArgumentException("Unsupported type: ${it::class.simpleName}")
            }
        }.toTypedArray()
    )
}

fun Display.asWidget(): Button {
    return Widgets.button()
        .withTexture(null)
        .withSize(this.getWidth(), this.getHeight())
        .withRenderer { graphics, context, _ ->
            val offsetX = (context.width - this.getWidth()) / 2f
            val offsetY = (context.height - this.getHeight()) / 2f
            this.render(graphics, context.x, context.y, offsetX, offsetY)
        }
}