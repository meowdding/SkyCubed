package tech.thatgravyboat.skycubed.features.overlays

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Alignment
import me.owdding.lib.displays.Displays
import me.owdding.lib.displays.toColumn
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
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.config.overlays.TrophyFishOverlayConfig
import tech.thatgravyboat.skycubed.utils.CachedValue
import kotlin.time.Duration.Companion.seconds

@Module
object TrophyFishOverlay : Overlay {

    override val name: Component = Text.of("Trophy Fish Overlay")
    override val position: Position get() = OverlayPositions.trophyFish
    override val bounds get() = display.getWidth() to display.getHeight()
    override val enabled: Boolean get() = TrophyFishOverlayConfig.enabled && SkyBlockIsland.CRIMSON_ISLE.inIsland()

    private val displayValue = CachedValue(5.seconds) {
        TrophyFishType.entries.map { it.createDisplay() }.toColumn()
    }
    private val display by displayValue

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        graphics.fill(0, 0, SackOverlay.bounds.first, SackOverlay.bounds.second, 0x50000000)
        display.render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        if (KnownMods.SKYBLOCK_PV.installed) {
            it.button(Text.of("Open SkyBlockPv to update data")) {
                McClient.self.connection?.sendCommand("pv ${McPlayer.name}")
            }
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            SackOverlay.position.reset()
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
            string(a.toFormattedString()) {
                withStyle(t.nameSuffix.style)
            }
        }
        string(caught.sumOf { it.value }.toFormattedString()) {
            color = TextColor.GRAY
        }
    }

    @OptIn(SkyBlockPvRequired::class)
    @Subscription(event = [TrophyFishCaughtEvent::class, SkyBlockPvOpenedEvent::class, ContainerCloseEvent::class, ProfileChangeEvent::class])
    @OnlyIn(SkyBlockIsland.CRIMSON_ISLE)
    fun onInvalidate() = displayValue.invalidate()
}
