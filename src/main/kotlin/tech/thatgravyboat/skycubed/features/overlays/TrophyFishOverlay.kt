package tech.thatgravyboat.skycubed.features.overlays

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Alignment
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.toColumn
import me.owdding.lib.displays.withTooltip
import me.owdding.lib.overlays.ConfigPosition
import me.owdding.lib.utils.KnownMods
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishTier
import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishType
import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishingAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.location.isle.TrophyFishCaughtEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.TrophyFishOverlayConfig
import tech.thatgravyboat.skycubed.utils.*
import kotlin.time.Duration.Companion.seconds

@Module
@RegisterOverlay
object TrophyFishOverlay : SkyCubedOverlay {

    private val config get() = TrophyFishOverlayConfig

    private val title by lazy {
        DisplayFactory.horizontal {
            string("Trophy Fish") {
                color = TextColor.ORANGE
                bold = true
            }
            if (!KnownMods.SKYBLOCK_PV.installed) {
                display(
                    Displays.text(
                        Text.of(" ⓘ") {
                            color = TextColor.GRAY
                        },
                    ).withTooltip {
                        add("Tip: With SkyBlockPv installed,") { color = TextColor.GRAY }
                        add("you can easily update your Trophy Fish data") { color = TextColor.GRAY }
                        add("by opening your own profile.") { color = TextColor.GRAY }
                    },
                )
            }
        }
    }

    override val name: Component = Text.of("Trophy Fish Overlay")
    override val position: ConfigPosition get() = OverlayPositions.trophyFish
    override val bounds get() = display.getWidth() to display.getHeight()
    override val enabled: Boolean get() = config.enabled && SkyBlockIsland.CRIMSON_ISLE.inIsland()
    override val background: OverlayBackground get() = config.background

    private val display by CachedValue(5.seconds) {
        listOf(title, *TrophyFishType.entries.map { it.createDisplay() }.toTypedArray()).toColumn()
    }

    override fun renderWithBackground(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        display.render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        if (KnownMods.SKYBLOCK_PV.installed) {
            it.button(Text.of("Open SkyBlockPv to update data")) {
                McClient.sendClientCommand("sbpv pv ${McPlayer.name}")
            }
        }
        val text = when (config.background) {
            OverlayBackground.TEXTURED -> "Textured Background"
            OverlayBackground.COLORED -> "Colored Background"
            OverlayBackground.NO_BACKGROUND -> "No Background"
        }
        it.button(Text.of(text)) {
            config.background = config.background.next()
            this::display.invalidateCache()
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.resetPosition()
        }
    }

    fun TrophyFishType.createDisplay() = DisplayFactory.horizontal(5, Alignment.CENTER) {
        val caught = TrophyFishingAPI.getCaught(this@createDisplay).toMutableMap().apply {
            TrophyFishTier.entries.forEach {
                this.computeIfAbsent(it) { 0 }
            }
        }.entries.sortedBy { it.key.ordinal }.filter { it.key != TrophyFishTier.NONE }

        display(Displays.item(this@createDisplay.diamond))
        caught.forEach { (t, a) ->
            val unlocked = a > 0
            if (config.hideUnlocked && unlocked) return@forEach
            if (config.showNumbers) {
                string(a.toFormattedString()) {
                    withStyle(t.nameSuffix.style)
                }
            } else {
                string("✔".takeIf { unlocked } ?: "✘") {
                    withStyle(t.nameSuffix.style)
                }
            }
        }

        if (config.showTotal) {
            string(caught.sumOf { it.value }.toFormattedString()) {
                color = TextColor.GRAY
            }
        }
    }

    @OptIn(SkyBlockPvRequired::class)
    @Subscription(event = [TrophyFishCaughtEvent::class, SkyBlockPvOpenedEvent::class, ContainerCloseEvent::class, ProfileChangeEvent::class])
    @OnlyIn(SkyBlockIsland.CRIMSON_ISLE)
    fun onInvalidate() = ::display.invalidateCache()
}
