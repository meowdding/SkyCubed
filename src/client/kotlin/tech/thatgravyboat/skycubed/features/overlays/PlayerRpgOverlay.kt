package tech.thatgravyboat.skycubed.features.overlays

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.profile.StatsAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig
import tech.thatgravyboat.skycubed.utils.blitSpritePercentX
import tech.thatgravyboat.skycubed.utils.drawScaledString

private const val WIDTH = 119
private const val HEIGHT = 48

object PlayerRpgOverlay : Overlay {

    private val BASE = SkyCubed.id("rpg/base")
    private val HEALTH = SkyCubed.id("rpg/health/normal")
    private val ABSORPTION = SkyCubed.id("rpg/health/absorption")
    private val MANA = SkyCubed.id("rpg/mana/normal")
    private val MANA_DEPLETED = SkyCubed.id("rpg/mana/depleted")
    private val MANA_NEEDED = SkyCubed.id("rpg/mana/needed")
    private val XP = SkyCubed.id("rpg/xp")

    private val AIR_BASE = SkyCubed.id("rpg/air/base")
    private val AIR = SkyCubed.id("rpg/air/normal")

    override val name: Component = Text.of("Player RPG Hud")
    override val enabled: Boolean get() = OverlaysConfig.rpgEnabled
    override val position: Position = OverlayPositions.rpg
    override val bounds: Pair<Int, Int> get() = WIDTH to HEIGHT

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val healthPercent = StatsAPI.health.toFloat() / StatsAPI.maxHealth.toFloat()
        val absorptionPercent = healthPercent - 1f
        val manaPercent = StatsAPI.mana.toFloat() / StatsAPI.maxMana.toFloat()
        val xpPercent = McPlayer.xpLevelProgress
        val airPercent = McPlayer.air.toFloat() / McPlayer.maxAir.toFloat()
        val manaUsePercent = (McPlayer.heldItem.getData(DataTypes.RIGHT_CLICK_MANA_ABILITY)?.second?.toFloat() ?: 0f) / StatsAPI.maxMana.toFloat()

        graphics.blitSprite(RenderType::guiTextured, BASE, 0, 0, WIDTH, HEIGHT)
        graphics.blitSpritePercentX(HEALTH, 47, 23, 70, 5, healthPercent.coerceIn(0f, 1f))
        graphics.blitSpritePercentX(ABSORPTION, 47, 23, 70, 5, absorptionPercent.coerceIn(0f, 1f))
        graphics.blitSpritePercentX(MANA_DEPLETED, 47, 18, 57, 4, manaUsePercent.coerceIn(0f, 1f))
        graphics.blitSpritePercentX(MANA, 47, 18, 57, 4, manaPercent.coerceIn(0f, 1f))
        graphics.blitSpritePercentX(MANA_NEEDED, 47, 18, 57, 4, manaUsePercent.coerceAtMost(manaPercent).coerceIn(0f, 1f))
        graphics.blitSpritePercentX(XP, 47, 29, 67, 4, xpPercent.coerceIn(0f, 1f))
        graphics.drawScaledString("${McPlayer.xpLevel}", 3, 33, 16, 0x78EC20)

        if (airPercent < 1f) {
            graphics.blitSprite(RenderType::guiTextured, AIR_BASE, 38, 34, 64, 6)
            graphics.blitSpritePercentX(AIR, 40, 34, 60, 4, airPercent.coerceIn(0f, 1f))
        }
    }

}