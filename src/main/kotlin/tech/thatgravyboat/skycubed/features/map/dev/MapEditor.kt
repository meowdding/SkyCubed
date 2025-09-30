package tech.thatgravyboat.skycubed.features.map.dev

import com.mojang.serialization.JsonOps
import me.owdding.ktmodules.Module
import net.minecraft.client.player.RemotePlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.ArmorStand
import org.joml.Vector3i
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.LeftClickEntityEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.LivingEntityRenderEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.api.accessors.glow
import tech.thatgravyboat.skycubed.api.accessors.glowColor
import tech.thatgravyboat.skycubed.api.conditions.Condition
import tech.thatgravyboat.skycubed.features.map.IslandData
import tech.thatgravyboat.skycubed.features.map.Maps
import tech.thatgravyboat.skycubed.features.map.dev.skins.SkinSelector
import tech.thatgravyboat.skycubed.features.map.pois.ConditionalPoi
import tech.thatgravyboat.skycubed.features.map.pois.EffigyPoi
import tech.thatgravyboat.skycubed.features.map.pois.NpcPoi
import tech.thatgravyboat.skycubed.features.map.pois.PortalPoi
import tech.thatgravyboat.skycubed.utils.CachedValue
import java.nio.file.StandardOpenOption
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

@Module
object MapEditor {

    var enabled = false
        private set
    val pois by CachedValue(1.seconds) {
        Maps.currentIsland?.pois?.associateBy { it.position } ?: emptyMap()
    }


    fun Entity.posAsVec3i(): Vector3i {
        val pos = this.position()
        return Vector3i(pos.x.roundToInt(), pos.y.roundToInt(), pos.z.roundToInt())
    }

    private operator fun <T> Map<Vector3i, T>.get(entity: Entity?): T? {
        entity ?: return null
        return this[entity.posAsVec3i()] ?: this[entity.posAsVec3i().apply { y = -1 }]
    }

    @Subscription
    private fun RegisterCommandsEvent.onRegister() {
        registerWithCallback("skycubed dev mapEditMode") {
            enabled = !enabled
            if (enabled) {
                Text.of("Toggled map edit mode on!") { this.color = TextColor.GREEN }.send()
            } else {
                Text.of("Toggled map edit mode off!") { this.color = TextColor.RED }.send()
            }
        }
        registerWithCallback("skycubed dev saveMap") {
            Maps.groups.forEach { (key, values) ->
                Text.of("Saving ${values.size} for $key to .minecraft/config/skycubed/maps/$key.json").send()
                val json = if (values.size == 1) {
                    IslandData.CODEC.encodeStart(JsonOps.INSTANCE, values[0])
                } else {
                    IslandData.CODEC.listOf().encodeStart(JsonOps.INSTANCE, values)
                }

                json.ifError {
                    Text.of("Failed to save $key, see logs for more details!").send()
                    SkyCubed.error(it.message())
                }
                json.ifSuccess { it ->

                    McClient.config.resolve("skycubed/maps/$key.json").createParentDirectories()
                        .writeText(
                            it.toPrettyString(),
                            Charsets.UTF_8,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.CREATE,
                        )
                }
            }
        }
    }

    @Subscription
    private fun LivingEntityRenderEvent.onRender() {
        if (!enabled) return
        val entity = entity ?: return
        if (entity.uuid.version() == 4) return
        if (this.entity is ArmorStand) return
        entity.glow = true

        val poi = pois[entity]

        if (poi is PortalPoi || poi is EffigyPoi) return
        entity.glowColor = if (entity is RemotePlayer) {
            if (poi != null) 0xFF00 else 0xFF0000
        } else {
            if (poi != null) 0xFF00 else 0xFF
        }
        if (poi != null && poi.position.y == -1) {
            Text.of("Syncing y for ${if (poi is NpcPoi) poi.name else poi.tooltip.firstOrNull()?.string ?: "<${poi.position}>"}")
                .send()
            poi.position.y = entity.y.roundToInt()
        }

    }

    @Subscription
    private fun LeftClickEntityEvent.modifyOrCreateNpc() {
        if (!enabled) return
        this.cancel()
        val poi = pois[entity]
        val mapPois = Maps.currentIsland?.pois
        if (poi != null && mapPois != null) {
            McClient.setScreenAsync { MapPoiEditScreen(poi, mapPois, entity = entity) }
            return
        }

        val npc = NpcPoi(
            entity.texture,
            "https://wiki.hypixel.net/\$name",
            "unknown",
            mutableListOf(
                "\$name",
                "",
                "§7§lClick to view wiki!",
            ),
            entity.posAsVec3i(),
        )

        if (McScreen.isShiftDown) {
            ConditionalPoi(Condition.TRUE, Condition.FALSE, npc).also {

                val pois = Maps.currentIsland?.pois ?: run {
                    Text.of("Unknown island").send()
                    return
                }
                Text.of("Created new conditional poi").send()
                pois.add(it)
                McClient.setScreenAsync { MapPoiEditScreen(npc, pois, entity = entity) }
            }
            return
        }

        npc.also {

            val pois = Maps.currentIsland?.pois ?: run {
                Text.of("Unknown island").send()
                return
            }
            Text.of("Created new poi").send()
            pois.add(it)
            McClient.setScreenAsync { MapPoiEditScreen(it, pois, entity = entity) }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private val Entity.texture: String
        get() = runCatching { SkinSelector.getSkin(this) }.getOrElse {
            it.printStackTrace()
            "Failure :("
        }

}
