package tech.thatgravyboat.skycubed.api.repo

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.repo.RemoteRepo
import me.owdding.skycubed.generated.SkyCubedCodecs
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData

@Module
object SackCodecs {
    var sackItems: Map<String, ItemStack> = emptyMap()
        private set

    @Subscription
    fun onRepo(event: FinishRepoLoadingEvent) {
        val repoData = RemoteRepo.getFileContentAsJson("sacks").toData(SkyCubedCodecs.getCodec<Sack>().listOf()) ?: emptyList()
        sackItems = repoData.flatMap { it.items }.map { it to RepoItemsAPI.getItem(it) }.sortedBy { (_, v) -> v.cleanName }.toMap()
    }

    @GenerateCodec
    data class Sack(
        val sack: String,
        val items: List<String>,
    ) {
        val item by lazy { RepoItemsAPI.getItem(sack) }
    }
}
