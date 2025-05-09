package tech.thatgravyboat.skycubed.features.dungeonmap

import net.minecraft.client.player.AbstractClientPlayer
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonClass
import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonPlayer
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skycubed.features.dungeonmap.position.DungeonPosition
import tech.thatgravyboat.skycubed.features.dungeonmap.position.WorldPosition
import java.util.*
import kotlin.math.roundToInt

class DungeonPlayer(
    var name: String,
    var classLevel: Int,
    var selectedClass: DungeonClass?,
    val instance: DungeonInstance,
    val position: DungeonPosition<*> = WorldPosition(0, 0, instance),
    var isDead: Boolean = false,

    ) {

    val oldPosition: DungeonPosition<*> = position.copy()

    var rotationOld: Int = 0
    var rotation: Int = 0
        set(value) {
            rotationOld = field
            field = value
        }

    private var player: AbstractClientPlayer? = null
    private var uuid: UUID? = null
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
                this.player = it as? AbstractClientPlayer
            }?.uuid
    }

    fun update(player: DungeonPlayer) {
        player.classLevel?.let { this.classLevel = it }
        player.dungeonClass?.let { this.selectedClass = it }
        this.isDead = player.dead
    }

    fun setPosition(mapPosition: DungeonPosition<*>) {
        this.oldPosition.set(this.position)
        this.position.set(mapPosition.inWorldSpace())
    }

    fun setRotation(rotation: Byte) {
        this.rotation = ((rotation * (360 / 16f)) % 360).roundToInt()
    }

    fun getPlayer(): AbstractClientPlayer? {
        if (uuid == null) return null
        return player
    }

}
