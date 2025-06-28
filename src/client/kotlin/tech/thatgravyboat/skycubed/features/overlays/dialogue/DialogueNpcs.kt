package tech.thatgravyboat.skycubed.features.overlays.dialogue

import kotlinx.coroutines.runBlocking
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skycubed.SkyCubed

data class DialogueNpc(
    val type: String = "",
    val durationModifier: Float = 1f,
)

@Module
object DialogueNpcs {

    private var npcs = mapOf<String, DialogueNpc>()
    private val default = DialogueNpc()

    init {
        runBlocking {
            try {
                npcs = SkyCubed.loadFromRepo<Map<String, DialogueNpc>>("npcs") ?: emptyMap()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    fun get(name: String): DialogueNpc {
        return npcs[name.lowercase()] ?: default
    }
}
