package tech.thatgravyboat.skycubed.features.overlays.dialogue

import com.google.gson.JsonParser
import me.owdding.ktmodules.Module
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.util.ProblemReporter
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.level.storage.TagValueOutput
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.level.LeftClickEntityEvent
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEntityEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.features.map.IslandData
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.pois.NpcPoi
import tech.thatgravyboat.skycubed.features.overlays.dialogue.DialogueOverlay.enabled
import tech.thatgravyboat.skycubed.utils.DisplayEntityPlayer
import tech.thatgravyboat.skycubed.utils.getSkin
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

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
        val level = McLevel.selfOrNull ?: return null
        val entity = lastClickedEntities.keys.find { npc -> level.getEntitiesOfClass(ArmorStand::class.java, npc.boundingBox).any { it.customName?.stripped == name } }
        if (entity != null && entity.isAlive) {
            lastClickedEntities[entity] = System.currentTimeMillis()

            if (entity is Player) {
                val equipment = EquipmentSlot.entries.map { entity.getItemBySlot(it).copy() }

                val textureUrl = runCatching {
                    entity.gameProfile.properties.get("textures").first().value().let {
                        JsonParser.parseString(Base64.decode(it).decodeToString()).getPath("textures.SKIN.url").asString("")
                    }
                }.getOrElse { "" }

                val skinFuture = if (textureUrl.isNotEmpty()) {
                    McClient.self.skinManager.getSkin(textureUrl)
                } else {
                    npcCache[name.trim().lowercase()]?.skin
                }

                if (skinFuture != null) {
                    return DisplayEntityPlayer(skinFuture, equipment, false)
                }
            } else {
                val output = TagValueOutput.createWithContext(
                    ProblemReporter.DISCARDING,
                    level.registryAccess(),
                )
                entity.saveWithoutId(output)

                val nbt = output.buildResult()
                if (nbt is CompoundTag) {
                    nbt.remove("UUID")
                }

                val copy = entity.type.create(level, EntitySpawnReason.EVENT)
                if (copy != null) {
                    val input = TagValueInput.create(
                        ProblemReporter.DISCARDING,
                        level.registryAccess(),
                        nbt,
                    )
                    copy.load(input)
                    return copy as LivingEntity
                }
            }
        }

        val customEntity = if (npc.type.isNotEmpty()) {
            Identifier.tryParse(npc.type)?.let { id ->
                BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null)
                    ?.runCatching { this.create(level, EntitySpawnReason.EVENT) }
                    ?.getOrNull()
            }
        } else null

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
