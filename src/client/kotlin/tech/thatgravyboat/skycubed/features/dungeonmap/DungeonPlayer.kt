package tech.thatgravyboat.skycubed.features.dungeonmap

import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.player.Player
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonClass
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonPlayer
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.features.dungeonmap.position.DungeonPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.WorldPosition
import java.util.*

class DungeonPlayer(
    var name: String,
    var classLevel: Int,
    var selectedClass: DungeonClass?,
    val instance: DungeonInstance,
    val position: DungeonPosition<*> = WorldPosition(0, 0, instance),
    var isDead: Boolean = false,

) {

    var rotation: Int = 0
    var uuid: UUID? = null
        get() {
            if (field == null) {
                field = tryFindUUID()
            }
            return field
        }

    private fun tryFindUUID(): UUID? {
        if (!McLevel.hasLevel) return null
        return McLevel.self.players().filter { it.name != null }
            .firstOrNull { it.name.stripped.equals(name, ignoreCase = true) }?.also {
                this.player = it
            }?.uuid
    }

    fun update(player: DungeonPlayer) {
        player.classLevel?.let { this.classLevel = it }
        player.dungeonClass?.let { this.selectedClass = it }
        this.isDead = player.dead
    }

    fun setPosition(mapPosition: DungeonPosition<*>) {
        this.position.set(mapPosition.inWorldSpace())
    }

    fun setRotation(rotation: Byte) {
        this.rotation = Math.round((rotation * (360 / 16f)) % 360)
    }

    var player: Player? = null
    val isSelf: Boolean get() = player is LocalPlayer

}