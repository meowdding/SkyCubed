package tech.thatgravyboat.skycubed.features.overlays

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.ktmodules.Module
import me.owdding.lib.builder.DisplayFactory
import me.owdding.lib.displays.Alignment
import me.owdding.lib.overlays.ConfigPosition
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.entity.NameChangedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.api.ExtraDisplays
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.PowerOrbOverlayConfig
import tech.thatgravyboat.skycubed.utils.*
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Module
@RegisterOverlay
object PowerOrbOverlay : SkyCubedOverlay {

    override val name: Component = Text.of("Power Orb")
    override val position: ConfigPosition get() = OverlayPositions.powerOrb
    override val actualBounds get() = display.getWidth() to display.getHeight()
    override val enabled: Boolean get() = LocationAPI.isOnSkyBlock && PowerOrbOverlayConfig.enabled && orbs.isNotEmpty()
    override val background: OverlayBackgroundConfig get() = PowerOrbOverlayConfig.background

    private var orbs: MutableMap<Entity, OrbInfo> = mutableMapOf()
    private val ARROWS = arrayOf("⬆", "⬈", "➡", "⬊", "⬇", "⬋", "⬅", "⬉")

    private val display by CachedValue(0.25.seconds) {
        DisplayFactory.vertical {
            orbs = orbs.filter {
                it.value.timeLeft > Duration.ZERO || it.key.isAlive
            }.toMutableMap()
            val (entity, orb) = orbs.toList().sortedBy { it.second.deployable.ordinal }.maxByOrNull { it.second.deployable.ordinal } ?: return@vertical
            horizontal(5, alignment = Alignment.CENTER) {
                //item(orb.deployable.item, 20, 20)
                display(ExtraDisplays.spinningItem(orb.deployable.item, scale = 20 / 16f))

                vertical(alignment = Alignment.CENTER) {
                    string(orb.deployable.item.hoverName)
                    val range = orb.deployable.range
                    val distance = sqrt(McPlayer.distanceSqr(entity.position()))
                    val inRange = distance <= range
                    string(Text.of(if (inRange) "In Range" else "Out of Range", if (inRange) TextColor.GREEN else TextColor.RED))
                }

                val timeColor = when {
                    orb.timeLeft.inWholeSeconds <= 5 -> TextColor.RED
                    orb.timeLeft.inWholeSeconds <= 15 -> TextColor.YELLOW
                    else -> TextColor.GREEN
                }
                string(Text.of("${orb.timeLeft.inWholeSeconds}s", timeColor))

                if (!PowerOrbOverlayConfig.arrow) return@horizontal
                val playerPos = McPlayer.self?.position() ?: return@horizontal
                val entityPos = entity.position()
                val angleToOrb = Math.toDegrees(atan2(playerPos.x - entityPos.x, entityPos.z - playerPos.z))
                var relativeYaw = (angleToOrb - McPlayer.self!!.yRot) % 360
                if (relativeYaw < 0) relativeYaw += 360
                val index = (((relativeYaw + 22.5) % 360) / 45).toInt()
                val arrowChar = ARROWS[index % 8]
                string(arrowChar) {
                    bold = true
                    color = TextColor.GRAY
                }
            }
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        display.render(graphics)
    }

    override fun onRightClick() = ContextMenu.open {
        val text = when (PowerOrbOverlayConfig.background) {
            OverlayBackgroundConfig.TEXTURED -> "Textured Background"
            OverlayBackgroundConfig.TRANSLUCENT -> "Translucent Background"
            OverlayBackgroundConfig.NO_BACKGROUND -> "No Background"
        }
        it.button(Text.of(text)) {
            PowerOrbOverlayConfig.background = PowerOrbOverlayConfig.background.next()
            this::display.invalidateCache()
        }
        it.button(Text.of("Toggle Arrow")) {
            PowerOrbOverlayConfig.arrow = !PowerOrbOverlayConfig.arrow
            this::display.invalidateCache()
        }
        it.divider()
        it.dangerButton(Text.of("Reset Position")) {
            position.resetPosition()
        }
    }

    @Subscription
    fun onEntityAdd(event: NameChangedEvent) {
        val entity = event.infoLineEntity
        Deployable.entries.forEach {
            it.regex.match(event.literalComponent, "seconds") { (time) ->
                val info = OrbInfo(it, time.toIntValue().seconds)
                orbs[entity] = info
            }
        }
    }

    @Subscription(ServerDisconnectEvent::class, ServerChangeEvent::class)
    fun onLeave() = orbs.clear()

    private enum class Deployable(@Language("RegExp") title: String, val range: Int) {
        RADIANT_POWER_ORB("^Radiant", 18),
        MANA_FLUX_POWER_ORB("^Mana Flux", 18),
        OVERFLUX_POWER_ORB("^Overflux", 18),
        PLASMAFLUX_POWER_ORB("^Plasmaflux", 20),
        ;

        val regex = "$title (?<seconds>\\d+)s".toRegex()
        val item by lazy { RepoItemsAPI.getItem(name) }
    }

    private data class OrbInfo(val deployable: Deployable, val timeLeft: Duration)
}

