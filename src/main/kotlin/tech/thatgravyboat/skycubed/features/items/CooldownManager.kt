package tech.thatgravyboat.skycubed.features.items

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEvent
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skycubed.config.items.ItemsConfig

@Module
object CooldownManager {

    private val cooldowns: MutableMap<String, Pair<Long, Long>> = mutableMapOf()

    @Subscription
    fun onItemRightClick(event: RightClickEvent) {
        val ability = event.stack.getData(DataTypes.COOLDOWN_ABILITY)
        if (ability != null) {
            addCooldown(ability.first, ability.second.inWholeMilliseconds)
        } else if (event.stack.getData(DataTypes.ID) == "GRAPPLING_HOOK" && !isWearingBatPerson()) {
            addCooldown("Grappling Hook", 2000)
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
