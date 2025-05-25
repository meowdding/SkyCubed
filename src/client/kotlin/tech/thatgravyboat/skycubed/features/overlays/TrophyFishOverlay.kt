package tech.thatgravyboat.skycubed.features.overlays

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.*
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
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.api.overlays.RegisterOverlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.config.overlays.TrophyFishOverlayConfig
import tech.thatgravyboat.skycubed.utils.CachedValue
import tech.thatgravyboat.skycubed.utils.SkyCubedTextures
import tech.thatgravyboat.skycubed.utils.invalidateCache
import kotlin.time.Duration.Companion.seconds

@Module
@RegisterOverlay
object TrophyFishOverlay : Overlay {

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
    override val position: Position get() = OverlayPositions.trophyFish
    override val bounds get() = display.getWidth() to display.getHeight()
    override val enabled: Boolean get() = config.enabled && SkyBlockIsland.CRIMSON_ISLE.inIsland()

    private val display by CachedValue(5.seconds) {
        val display = listOf(title, *TrophyFishType.entries.map { it.createDisplay() }.toTypedArray()).toColumn().withPadding(2)
        if (config.background) {
            Displays.background(SkyCubedTextures.backgroundBox, display)
        } else display
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.fill(0, 0, bounds.first, bounds.second, 0x50000000)
        display.render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        if (KnownMods.SKYBLOCK_PV.installed) {
            it.button(Text.of("Open SkyBlockPv to update data")) {
                // TODO: remove backwards compat with 1.21.6 or 1.22
                val command = if (KnownMods.SKYBLOCK_PV.version!! > "1.2.0") "sbpv pv" else "pv"
                McClient.sendCommand("$command ${McPlayer.name}")
            }
        }
        it.button(Text.of("${if (config.background) "Disable" else "Enable"} Custom Background")) {
            config.background = !config.background
            ::display.invalidateCache()
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.reset()
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
