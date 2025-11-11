package tech.thatgravyboat.skycubed.features.overlays.rpg

import earth.terrarium.olympus.client.ui.context.ContextMenu
import me.owdding.lib.overlays.ConfigPosition
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffects
import tech.thatgravyboat.skyblockapi.api.area.mining.GlaciteAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.StatsAPI
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.drawSprite
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.config.overlays.PlayerDisplay
import tech.thatgravyboat.skycubed.config.overlays.RpgOverlayConfig
import tech.thatgravyboat.skycubed.utils.*

private const val WIDTH = 119
private const val HEIGHT = 48

@RegisterOverlay
object PlayerRpgOverlay : SkyCubedOverlay {

    private val BASE = SkyCubed.id("rpg/base")
    private val HEALTH_NORMAL = SkyCubed.id("rpg/health/normal")
    private val HEALTH_POISON = SkyCubed.id("rpg/health/poison")
    private val HEALTH_WITHER = SkyCubed.id("rpg/health/wither")
    private val HEALTH_FREEZE = SkyCubed.id("rpg/health/freeze")
    private val ABSORPTION = SkyCubed.id("rpg/health/absorption")
    private val MANA = SkyCubed.id("rpg/mana/normal")
    private val MANA_DEPLETED = SkyCubed.id("rpg/mana/depleted")
    private val MANA_NEEDED = SkyCubed.id("rpg/mana/needed")
    private val XP = SkyCubed.id("rpg/xp")
    private val SKYBLOCK_XP = SkyCubed.id("rpg/skyblock_xp")

    private val AIR_BASE = SkyCubed.id("rpg/air/base")
    private val AIR = SkyCubed.id("rpg/air/normal")

    override val name: Component = Text.of("Player RPG Hud")
    override val enabled: Boolean get() = LocationAPI.isOnSkyBlock && RpgOverlayConfig.enabled
    override val position: ConfigPosition = OverlayPositions.rpg
    override val bounds: Pair<Int, Int> get() = WIDTH to HEIGHT

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val healthPercent = StatsAPI.health.toFloat() / StatsAPI.maxHealth.toFloat()
        val absorptionPercent = healthPercent - 1f
        val manaPercent = StatsAPI.mana.toFloat() / StatsAPI.maxMana.toFloat()
        val xpPercent = McPlayer.xpLevelProgress
        val skyblockLevelPercent = ProfileAPI.sbLevelProgress / 100f
        val airPercent = McPlayer.air.toFloat() / McPlayer.maxAir.toFloat()
        val manaUsePercent = (McPlayer.heldItem.getData(DataTypes.RIGHT_CLICK_MANA_ABILITY)?.second?.toFloat() ?: 0f) / StatsAPI.maxMana.toFloat()

        val healthSprite = when {
            McPlayer.self?.hasEffect(MobEffects.POISON) == true -> HEALTH_POISON
            McPlayer.self?.hasEffect(MobEffects.WITHER) == true -> HEALTH_WITHER
            GlaciteAPI.inGlaciteTunnels() && GlaciteAPI.cold > OverlaysConfig.coldOverlay -> HEALTH_FREEZE
            else -> HEALTH_NORMAL
        }

        graphics.drawSprite(BASE, 0, 0, WIDTH, HEIGHT)

        val player = McPlayer.self as? AbstractClientPlayer
        if (RpgOverlayConfig.playerDisplay != PlayerDisplay.DISABLED && player != null) {
            Utils.drawRpgPlayer(graphics, player, HEIGHT, HEIGHT, 30f)
        }

        val positions = RpgOverlayPositionHandler.positions

        graphics.blitSpritePercentX(healthSprite, positions.health, healthPercent)
        graphics.blitSpritePercentX(ABSORPTION, positions.health, absorptionPercent)

        graphics.blitSpritePercentX(MANA_DEPLETED, positions.mana, manaUsePercent)
        graphics.blitSpritePercentX(MANA, positions.mana, manaPercent)
        graphics.blitSpritePercentX(MANA_NEEDED, positions.mana, manaUsePercent.coerceAtMost(manaPercent))

        if (RpgOverlayConfig.skyblockLevel) {
            graphics.blitSpritePercentX(SKYBLOCK_XP, positions.xpBar, skyblockLevelPercent)
            graphics.drawScaledString("${ProfileAPI.sbLevel}", positions.xpText.x, positions.xpText.y, 16, 0x55FFFF)
        } else {
            graphics.blitSpritePercentX(XP, positions.xpBar, xpPercent)
            graphics.drawScaledString("${McPlayer.xpLevel}", positions.xpText.x, positions.xpText.y, 16, 0x78EC20)
        }

        if (airPercent < 1f) {
            graphics.drawSprite(AIR_BASE, positions.airBase)
            graphics.blitSpritePercentX(AIR, positions.airBar, airPercent)
        }
    }

    private fun GuiGraphics.drawSprite(rl: ResourceLocation, element: RpgOverlayPositionHandler.RpgOverlayElement) {
        drawSprite(rl, element.x, element.y, element.width, element.height)
    }

    private fun GuiGraphics.blitSpritePercentX(rl: ResourceLocation, element: RpgOverlayPositionHandler.RpgOverlayElement, percent: Float) {
        if (element.direction == "vertical") {
            blitSpritePercentY(rl, element.x, element.y, element.width, element.height, percent.coerceIn(0f, 1f))
        } else {
            blitSpritePercentX(rl, element.x, element.y, element.width, element.height, percent.coerceIn(0f, 1f))
        }
    }

    override fun onRightClick() = ContextMenu.open {
        if (SkyCubed.is1218) {
            val text = when (RpgOverlayConfig.playerDisplay) {
                PlayerDisplay.DISABLED -> "Show Armored Player"
                PlayerDisplay.ARMORED -> "Show Unarmored Player"
                PlayerDisplay.UNARMORED -> "Hide Player"
            }
            it.button(Text.of(text)) {
                RpgOverlayConfig.playerDisplay = RpgOverlayConfig.playerDisplay.next()
            }
            it.divider()
        }
        it.dangerButton(Text.of("Reset Position")) {
            position.resetPosition()
        }
    }
}
