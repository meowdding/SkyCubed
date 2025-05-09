package tech.thatgravyboat.skycubed.features.overlays

import me.owdding.lib.displays.Displays
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.profile.StatsAPI
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.api.overlays.Overlay
import tech.thatgravyboat.skycubed.config.overlays.OverlayPositions
import tech.thatgravyboat.skycubed.config.overlays.Position
import tech.thatgravyboat.skycubed.config.overlays.RpgOverlay
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayer
import tech.thatgravyboat.skycubed.utils.blitSpritePercentX
import tech.thatgravyboat.skycubed.utils.drawScaledString


private const val WIDTH = 119
private const val HEIGHT = 48

object PlayerRpgOverlay : Overlay {

    private val BASE = SkyCubed.id("rpg/base")
    private val MAIN_BACKGROUND = SkyCubed.id("rpg/base/main_background")
    private val BACKGROUND_OUTLINE = SkyCubed.id("rpg/base/background_outline")
    private val BARS = SkyCubed.id("rpg/base/bars")
    private val LEVEL = SkyCubed.id("rpg/base/level")
    private val PERSON = SkyCubed.id("rpg/base/person")

    private val HEALTH = SkyCubed.id("rpg/health/normal")
    private val ABSORPTION = SkyCubed.id("rpg/health/absorption")
    private val MANA = SkyCubed.id("rpg/mana/normal")
    private val MANA_DEPLETED = SkyCubed.id("rpg/mana/depleted")
    private val MANA_NEEDED = SkyCubed.id("rpg/mana/needed")
    private val XP = SkyCubed.id("rpg/xp")
    private val SKYBLOCK_XP = SkyCubed.id("rpg/skyblock_xp")

    private val AIR_BASE = SkyCubed.id("rpg/air/base")
    private val AIR = SkyCubed.id("rpg/air/normal")

    override val name: Component = Text.of("Player RPG Hud")
    override val enabled: Boolean get() = RpgOverlay.enabled
    override val position: Position = OverlayPositions.rpg
    override val bounds: Pair<Int, Int> get() = WIDTH to HEIGHT

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val healthPercent = StatsAPI.health.toFloat() / StatsAPI.maxHealth.toFloat()
        val absorptionPercent = healthPercent - 1f
        val manaPercent = StatsAPI.mana.toFloat() / StatsAPI.maxMana.toFloat()
        val xpPercent = McPlayer.xpLevelProgress
        val skyblockLevelPercent = ProfileAPI.sbLevelProgress / 100f
        val airPercent = McPlayer.air.toFloat() / McPlayer.maxAir.toFloat()
        val itemStackData = McPlayer.heldItem.getData(DataTypes.RIGHT_CLICK_MANA_ABILITY)
        val manaUsePercent = (itemStackData?.second?.toFloat() ?: 0f) / StatsAPI.maxMana.toFloat()


        graphics.blitSprite(RenderType::guiTextured, MAIN_BACKGROUND, 6, 3, 38, 41)

        if (RpgOverlay.actualPlayer) {
            graphics.blitSprite(RenderType::guiTextured, MAIN_BACKGROUND, 6, 3, 38, 41)

            val emptyArmor = listOf(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY)
            val entity = DisplayEntityPlayer(McPlayer.skin, emptyArmor)
            Displays.entity(entity, 28, 31, 30, 20f, 20f).render(graphics, 11, 30)
        } else {
            graphics.blitSprite(RenderType::guiTextured, PERSON, 11, 13, 28, 31)
        }

        graphics.pushPop {
            translate(0f, 0f, 100f)

            graphics.blitSprite(RenderType::guiTextured, BACKGROUND_OUTLINE, 3, 0, 44, 47)

            graphics.blitSprite(RenderType::guiTextured, LEVEL, 0, 26, 22, 22)
            graphics.blitSprite(RenderType::guiTextured, BARS, 47, 16, 72, 19)

            graphics.blitSpritePercentX(HEALTH, 47, 23, 70, 5, healthPercent.coerceIn(0f, 1f))
            graphics.blitSpritePercentX(ABSORPTION, 47, 23, 70, 5, absorptionPercent.coerceIn(0f, 1f))
            graphics.blitSpritePercentX(MANA_DEPLETED, 47, 18, 57, 4, manaUsePercent.coerceIn(0f, 1f))
            graphics.blitSpritePercentX(MANA, 47, 18, 57, 4, manaPercent.coerceIn(0f, 1f))
            val coercedManaPercent = manaUsePercent.coerceAtMost(manaPercent).coerceIn(0f, 1f)
            graphics.blitSpritePercentX(MANA_NEEDED, 47, 18, 57, 4, coercedManaPercent)

            if (RpgOverlay.skyblockLevel) {
                graphics.blitSpritePercentX(SKYBLOCK_XP, 47, 29, 67, 4, skyblockLevelPercent.coerceIn(0f, 1f))
                graphics.drawScaledString("${ProfileAPI.sbLevel}", 3, 33, 16, 0x55FFFF)
            } else {
                graphics.blitSpritePercentX(XP, 47, 29, 67, 4, xpPercent.coerceIn(0f, 1f))
                graphics.drawScaledString("${McPlayer.xpLevel}", 3, 33, 16, 0x78EC20)
            }

            if (airPercent < 1f) {
                graphics.blitSprite(RenderType::guiTextured, AIR_BASE, 38, 34, 64, 6)
                graphics.blitSpritePercentX(AIR, 40, 34, 60, 4, airPercent.coerceIn(0f, 1f))
            }
        }
    }

}
