package tech.thatgravyboat.skycubed.features.misc

import me.owdding.ktmodules.Module
import me.owdding.lib.extensions.shorten
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.entity.NameChangedEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.time.currentInstant
import tech.thatgravyboat.skyblockapi.utils.time.since
import tech.thatgravyboat.skycubed.config.rendering.RenderingConfig
import tech.thatgravyboat.skycubed.features.misc.CustomDamage.DamageType.Companion.damageType
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

@Module
object CustomDamage {
    private val config get() = RenderingConfig.customDamage

    private val damageRegex = Regex("([✧✯]?)([\\d,]+)+[✧✯]?([❤+⚔✷♞☄]?)")

    private val damageList = ConcurrentLinkedQueue<Damage>()

    @Subscription
    @OnlyOnSkyBlock
    fun onArmorStand(event: NameChangedEvent) {
        if (!config.enabled) return

        val entity = event.infoLineEntity as? ArmorStand ?: return

        val entityName = event.component

        damageRegex.matchEntire(entityName.string)?.let {
            val type = it.damageType(entityName.siblings[0]?.color)
            val amount = (it.groups[2]?.value ?: return).toLongValue()
            val close = if (config.combining) damageList.firstOrNull { existing ->
                existing.type == type && entity.position().distanceTo(existing.position) < config.combineThreshold
            } else null
            if (close == null) {
                damageList.add(
                    Damage(
                        amount,
                        type,
                        currentInstant(),
                        entity.position(),
                        currentInstant()
                    ),
                )
            } else {
                close.amount += amount
                if (close.actualSpawnTime.since() <= config.fullTimeout.milliseconds) {
                    close.time = currentInstant()
                }
            }
            McClient.runNextTick {
                entity.isCustomNameVisible = false
            }
        }
    }

    @Subscription(TickEvent::class)
    @OnlyOnSkyBlock
    fun onTick() {
        damageList.removeIf { it.time.since() > config.timeout.milliseconds }
    }

    @Subscription
    fun onRender(event: RenderWorldEvent.AfterTranslucent) {
        damageList.forEach { damage ->
            val progress = ((currentInstant() - damage.time) / config.timeout.milliseconds).coerceIn(0.0, 1.0)

            val damageText = Text.of(damage.amount.shorten(config.touchiness).addIcons(damage.type)) {
                this.color = damage.type.color
            }

            val scale = 0.03F

            var position = damage.position

            if (config.droppingTags) {
                val removal = sin(progress * Math.PI / 4)
                position = position.subtract(0.0, removal, 0.0)
            }

            event.atCamera {
                translate(
                    position.x + 0.5,
                    position.y + 1.07f,
                    position.z + 0.5
                )
                translate(0f, 0.5F * -scale, 0f)
                mulPose(event.cameraRotation)
                scale(scale, -scale, scale)
                val xOffset = -McFont.width(damageText) / 2.0f

                val alpha = if (progress < 0.7 || !config.fadingTags) {
                    1.0F
                } else {
                    1.0F - ((progress - 0.7F) / 0.3F).toFloat()
                }
                val uInt: UInt = ((alpha * 255).toInt() shl 24).toUInt() or 0x00FFFFFFu

                event.drawString(
                    damageText,
                    xOffset,
                    0.0F,
                    uInt,
                    config.shadow,
                    Font.DisplayMode.SEE_THROUGH,
                    0x00000000u,
                    LightTexture.FULL_BRIGHT
                )
            }
        }
    }

    private fun String.addIcons(type: DamageType): String {
        return when (config.icons) {
            IconMode.PREFIX -> "${type.icon} $this"
            IconMode.SUFFIX -> "$this ${type.icon}"
            IconMode.BOTH -> "${type.icon} $this ${type.icon}"
            else -> this
        }
    }

    private data class Damage(
        var amount: Long,
        val type: DamageType,
        var time: Instant,
        val position: Vec3,
        val actualSpawnTime: Instant
    )

    private enum class DamageType(val color: Int, val icon: String) {
        CRIT(0xf9e2af, "✧"),
        OVERLOAD_CRIT(0xf2cdcd, "✯"),
        LOVE(0xf5c2e7, "❤"),
        END_STONE(0xf5e0dc, "⚔"),
        CONTAGION(0xcba6f7, "💥"),
        LIGHTING(0x89dceb, "⚡"),
        FIRE(0xfab387, "🔥"),
        VENOMOUS(0xa6e3a1, "☣"),
        PET(0xf5c2e7, "☃"),
        TARANTULA(0xb4befe, "🕸️"),
        VOODOO(0xf38ba8, "🐡"),
        TRUE_DAMAGE(0xcdd6f4, "❂"),
        SUFFOCATION(0x89b4fa, "🫧"),
        ALIGNMENT(0x94e2d5, "🛡️"), // TODO: Dealt when sharing damage, probably when tanking 3 lines at the suffix + green
        BLAST(0xf38ba8, "💥"), // TODO: Dealt by some mobs idk which, gray color and sun-like symbol suffix
        ANTI_CHEESE(0xf9e2af, "🧀"), // TODO: Dealt by rev horror and sven when on 1 spot too long I think? green + upwards pointing arrow
        VOIDGLOOM(0x11111b, "🌑"), // TODO: Voidgloom T2+, ourple :3
        NORMAL(0x6c7086, "");

        companion object {
            fun MatchResult.damageType(color: Int?): DamageType = when {
                this.groups[3]?.value?.endsWith("❤") == true -> LOVE
                this.groups[3]?.value?.endsWith("⚔") == true -> END_STONE
                this.groups[3]?.value?.endsWith("+") == true -> TARANTULA
                this.groups[3]?.value == "✷" -> CONTAGION
                this.groups[3]?.value == "♞" -> PET
                this.groups[3]?.value == "☄" -> VOODOO
                this.groups[1]?.value == "✧" -> CRIT
                this.groups[1]?.value == "✯" -> OVERLOAD_CRIT
                else -> when(color) {
                    16755200 -> FIRE
                    43520 -> VENOMOUS
                    5592575 -> LIGHTING
                    16777215 -> TRUE_DAMAGE
                    43690 -> SUFFOCATION
                    else -> NORMAL
                }
            }
        }
    }
}

enum class IconMode {
    NONE,
    PREFIX,
    SUFFIX,
    BOTH,
}
