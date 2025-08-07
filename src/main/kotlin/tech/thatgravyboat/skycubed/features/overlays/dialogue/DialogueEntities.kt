package tech.thatgravyboat.skycubed.features.overlays.dialogue

import me.owdding.ktmodules.Module
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.level.LeftClickEntityEvent
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEntityEvent
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.features.map.IslandData
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.pois.NpcPoi
import tech.thatgravyboat.skycubed.features.overlays.dialogue.DialogueOverlay.enabled
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayer
import java.util.*
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.optionals.getOrNull

@Module
object DialogueEntities {

    private val npcCache by lazy {
        Maps.groups.values
            .flatten()
            .flatMap(IslandData::pois)
            .filterIsInstance<NpcPoi>()
            .associateBy { it.name.lowercase().stripColor() }
    }

    private var lastClickedEntities: WeakHashMap<LivingEntity, Long> = WeakHashMap()

    @Subscription
    @OnlyOnSkyBlock
    private fun RightClickEntityEvent.onRightClickEvent() = handleEntityClick(this.entity)

    @Subscription
    @OnlyOnSkyBlock
    private fun LeftClickEntityEvent.onLeftClickEvent() = handleEntityClick(this.entity)

    private fun handleEntityClick(event: Entity) {
        if (!enabled) return

        val entity = event as? LivingEntity ?: return
        lastClickedEntities[entity] = System.currentTimeMillis()
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun get(name: String, npc: DialogueNpc): LivingEntity? {
        val entity = lastClickedEntities.keys.find { npc ->
            McLevel.self.getEntitiesOfClass(ArmorStand::class.java, npc.boundingBox).any { it.customName?.stripped == name }
        }
        if (entity != null && entity.isAlive) {
            lastClickedEntities[entity] = System.currentTimeMillis()
            return entity
        }
        val customEntity = EntityType.byString(npc.type).getOrNull()?.runCatching { this.create(McLevel.self, EntitySpawnReason.EVENT) }?.getOrNull()
        if (customEntity is LivingEntity) {
            return customEntity
        }
        return npcCache[name.trim().lowercase()]?.let(NpcPoi::skin)?.let { DisplayEntityPlayer(it, listOf(), false) }
    }

    fun updateCache(cacheTime: Long) {
        lastClickedEntities.entries.removeIf {
            it.value + cacheTime < System.currentTimeMillis()
        }
    }
}
