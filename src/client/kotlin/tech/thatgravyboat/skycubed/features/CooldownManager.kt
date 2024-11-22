package tech.thatgravyboat.skycubed.features

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.BlockMinedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEvent
import tech.thatgravyboat.skyblockapi.api.profile.PetsAPI
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

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

    @Subscription
    fun onBlockMined(event: BlockMinedEvent) {
        val id = McPlayer.heldItem.getData(DataTypes.ID)
        if (id == "TREECAPITATOR_AXE" || id == "JUNGLE_AXE") {
            var reduction = 0.0
            if (PetsAPI.pet.equals("monkey", true) && PetsAPI.rarity != null && PetsAPI.rarity!! >= SkyBlockRarity.LEGENDARY) {
                reduction = PetsAPI.level * 0.005
            }
            addCooldown("TreeCapitator", 2000 - (2000 * reduction).toLong())
        }
    }

    private fun getCooldownId(stack: ItemStack): String? {
        return when (stack.getData(DataTypes.ID)) {
            "GRAPPLING_HOOK" -> "Grappling Hook"
            "TREECAPITATOR_AXE", "JUNGLE_AXE" -> "TreeCapitator"
            else -> stack.getData(DataTypes.COOLDOWN_ABILITY)?.first
        }
    }

    private fun isOnCooldown(ability: String): Boolean {
        return cooldowns[ability]?.let { it.second > System.currentTimeMillis() } ?: false
    }

    private fun isWearingBatPerson(): Boolean {
        val armor = McPlayer.self?.inventory?.armor ?: return false
        return armor[0].getData(DataTypes.ID) == "BAT_PERSON_BOOTS" &&
                armor[1].getData(DataTypes.ID) == "BAT_PERSON_LEGGINGS" &&
                armor[2].getData(DataTypes.ID) == "BAT_PERSON_CHESTPLATE" &&
                armor[3].getData(DataTypes.ID) == "BAT_PERSON_HELMET"
    }

    private fun addCooldown(ability: String, duration: Long) {
        if (isOnCooldown(ability)) return
        cooldowns[ability] = System.currentTimeMillis() to System.currentTimeMillis() + duration
    }

    fun getCooldown(stack: ItemStack): Float? {
        val id = getCooldownId(stack) ?: return null
        val cooldown = cooldowns[id] ?: return null
        val start = cooldown.first
        val end = cooldown.second
        val now = System.currentTimeMillis()
        return (if (now < end) (end - now).toFloat() / (end - start) else 0f).coerceIn(0f, 1f)
    }
}