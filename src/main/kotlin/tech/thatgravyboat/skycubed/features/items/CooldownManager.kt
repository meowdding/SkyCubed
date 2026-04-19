package tech.thatgravyboat.skycubed.features.items

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skycubed.config.items.ItemsConfig
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.api.profile.PetsAPI
import kotlin.math.roundToLong
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Module
object CooldownManager {

    private val cooldowns: MutableMap<String, Pair<Long, Long>> = mutableMapOf()
    val breakingPowerRegex = Regex("(?i)Breaking Power \\d+")

    @Subscription
    fun onItemRightClick(event: RightClickEvent) {
        val ability = event.stack.getData(DataTypes.COOLDOWN_ABILITY)
        if (ability != null) {
            if (hasBreakingPower(event.stack)) {
                val cooldownReduction = getPetCDRMultiplier() // TODO: add support for Mineshaft Mayhem and SkyMall
                val calculatedSeconds = (ability.second.inWholeMilliseconds * cooldownReduction).roundToLong()
                addCooldown(ability.first, calculatedSeconds)
            } else {
                addCooldown(ability.first, ability.second.inWholeMilliseconds)
            }
        } else if (event.stack.getData(DataTypes.ID) == "GRAPPLING_HOOK" && !isWearingBatPerson()) {
            addCooldown("Grappling Hook", 2000)
        }
    }

    private fun getPetCDRMultiplier(): Double {
        val pet = PetsAPI.pet
        val level = PetsAPI.level
        val rarity = PetsAPI.rarity

        return when (pet) {
            "Bal" -> if (rarity == SkyBlockRarity.LEGENDARY) 1.0 - level * (0.1 / 100.0) else 1.0
            "Crow" -> {
                val aboveRare = rarity == SkyBlockRarity.EPIC || rarity == SkyBlockRarity.LEGENDARY
                val baseMulti = if (aboveRare) 0.12 else 0.07
                1.0 - ((level * baseMulti) + 3.0) / 100.0
            }

            else -> 1.0
        }
    }

    private fun hasBreakingPower(stack: ItemStack): Boolean {
        return stack.getLore().any { line ->
            line.stripped.contains(breakingPowerRegex)
        }
    }

    private fun getCooldownId(stack: ItemStack): String? {
        return when (stack.getData(DataTypes.ID)) {
            "GRAPPLING_HOOK" -> "Grappling Hook"
            else -> stack.getData(DataTypes.COOLDOWN_ABILITY)?.first
        }
    }

    private fun isOnCooldown(ability: String): Boolean {
        return cooldowns[ability]?.let { it.second > System.currentTimeMillis() } == true
    }

    private fun isWearingBatPerson(): Boolean {
        return McPlayer.boots.getData(DataTypes.ID) == "BAT_PERSON_BOOTS" &&
            McPlayer.leggings.getData(DataTypes.ID) == "BAT_PERSON_LEGGINGS" &&
            McPlayer.chestplate.getData(DataTypes.ID) == "BAT_PERSON_CHESTPLATE" &&
            McPlayer.helmet.getData(DataTypes.ID) == "BAT_PERSON_HELMET"
    }

    private fun addCooldown(ability: String, duration: Long) {
        if (isOnCooldown(ability)) return
        cooldowns[ability] = System.currentTimeMillis() to System.currentTimeMillis() + duration
    }

    fun getCooldown(stack: ItemStack): Float? {
        if (!ItemsConfig.cooldowns) return null
        val id = getCooldownId(stack) ?: return null
        val cooldown = cooldowns[id] ?: return null
        val start = cooldown.first
        val end = cooldown.second
        val now = System.currentTimeMillis()
        return (if (now < end) (end - now).toFloat() / (end - start) else 0f).coerceIn(0f, 1f)
    }
}
