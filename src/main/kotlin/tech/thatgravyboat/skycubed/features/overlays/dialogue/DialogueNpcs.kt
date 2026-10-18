package tech.thatgravyboat.skycubed.features.overlays.dialogue

import com.mojang.serialization.Codec
import kotlinx.coroutines.runBlocking
import me.owdding.ktmodules.Module
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.repo.RemoteRepo
import me.owdding.skycubed.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skycubed.SkyCubed

data class DialogueNpc(
    val type: String = "",
)

@Module
object DialogueNpcs {

    private var npcs = mapOf<String, DialogueNpc>()
    private val default = DialogueNpc()

    @Subscription(FinishRepoLoadingEvent::class)
    private fun load() {
        npcs = RemoteRepo.getFileContentAsJson("skycubed/npcs").toData(CodecUtils.map(Codec.STRING, Codec.STRING.fieldOf("type").codec().xmap(::DialogueNpc, DialogueNpc::type))) ?: emptyMap()
    }

    fun get(name: String): DialogueNpc {
        return npcs[name.lowercase()] ?: default
    }
}
