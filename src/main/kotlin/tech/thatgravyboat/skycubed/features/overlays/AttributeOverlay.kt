package tech.thatgravyboat.skycubed.features.overlays

import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Alignment
import me.owdding.lib.displays.Displays
import me.owdding.lib.overlays.ConfigPosition
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.hunting.AttributeAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.config.overlays.AttributeOverlayConfig
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.features.screens.AttributeHudEditScreen
import tech.thatgravyboat.skycubed.utils.*
import kotlin.time.Duration.Companion.seconds

@RegisterOverlay
object AttributeOverlay : SkyCubedOverlay {

    override val name: Component = Text.of("Attribute Overlay")
    override val position: ConfigPosition get() = OverlayPositions.attribute
    override val actualBounds get() = display.getWidth() to display.getHeight()
    override val enabled: Boolean get() = LocationAPI.isOnSkyBlock && AttributeOverlayConfig.enabled && AttributeOverlayConfig.attributes.isNotEmpty()
    override val background: OverlayBackgroundConfig get() = AttributeOverlayConfig.background

    private val display by CachedValue(1.seconds) {
        if (AttributeOverlayConfig.attributes.isEmpty()) return@CachedValue Displays.empty(0, 0)

        DisplayFactory.horizontal(5, alignment = Alignment.CENTER) {
            AttributeOverlayConfig.elements.forEach { element ->
                vertical(alignment = Alignment.CENTER) {
                    when (element) {
                        AttributeElements.STACK -> display(Displays.item(Items.LEAD, McFont.height, McFont.height))
                        AttributeElements.NAME -> string(Text.of("Attribute", TextColor.GOLD))
                        AttributeElements.INTERNAL_NAME -> string(Text.of("SbId", TextColor.GOLD))
                        AttributeElements.OWNED -> string(Text.of("Owned", TextColor.GREEN))
                        AttributeElements.SYPHONED -> string(Text.of("Syphoned", TextColor.AQUA))
                        AttributeElements.LEVEL -> string(Text.of("Level", TextColor.PINK))
                    }

                    spacer(0, 4)

                    AttributeOverlayConfig.attributes.map { SkyBlockId.attribute(it) }.forEach { id ->
                        val data = AttributeAPI.attributeMap[id]

                        when (element) {
                            AttributeElements.STACK -> display(Displays.item(id.toItem(), McFont.height, McFont.height))
                            AttributeElements.NAME -> string(id.toItem().hoverName)
                            AttributeElements.INTERNAL_NAME -> string(Text.of(id.id.substringAfter(":"), id.toItem().hoverName.color))
                            AttributeElements.OWNED -> string(Text.of((data?.owned ?: 0).toFormattedString(), TextColor.GREEN))
                            AttributeElements.SYPHONED -> string(Text.of((data?.syphoned ?: 0).toFormattedString(), TextColor.AQUA))
                            AttributeElements.LEVEL -> string(Text.of((data?.level ?: 0).toFormattedString(), TextColor.PINK))
                        }
                    }
                }
            }
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        display.render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        it.button(Text.of("Open Attribute Edit Screen")) {
            McClient.setScreenAsync { AttributeHudEditScreen() }
        }
        val text = when (AttributeOverlayConfig.background) {
            OverlayBackgroundConfig.TEXTURED -> "Textured Background"
            OverlayBackgroundConfig.TRANSLUCENT -> "Translucent Background"
            OverlayBackgroundConfig.NO_BACKGROUND -> "No Background"
        }
        it.button(Text.of(text)) {
            AttributeOverlayConfig.background = AttributeOverlayConfig.background.next()
            this::display.invalidateCache()
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.resetPosition()
        }
    }

    enum class AttributeElements : Translatable {
        STACK,
        NAME,
        INTERNAL_NAME,
        OWNED,
        SYPHONED,
        LEVEL,
        ;

        override fun getTranslationKey(): String = "skycubed.config.overlays.attributes.elements.${name.lowercase()}"

        companion object {
            val DEFAULT = listOf(STACK, NAME, OWNED, LEVEL)
        }
    }
}
