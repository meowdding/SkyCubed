package tech.thatgravyboat.skycubed.api.repo

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.repo.RemoteRepo
import me.owdding.skycubed.generated.SkyCubedCodecs
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Module
object SackCodecs {
    var sackItems: Map<String, LazyItemStack> = emptyMap()
        private set

    @Subscription
    fun onRepo(event: FinishRepoLoadingEvent) {
        val repoData = RemoteRepo.getFileContentAsJson("sacks.json").toData(SkyCubedCodecs.getCodec<Sack>().listOf()) ?: emptyList()
        sackItems = repoData.flatMap { it.items }
            .mapNotNull { id -> SkyBlockItemsRepo.getLazyItemStack(id)?.let { id to it } }
            .sortedBy { (_, v) -> v.getDisplayName().stripped }.toMap()
    }

    @GenerateCodec
    data class Sack(
        val sack: String,
        val items: List<String>,
    ) {
        val item get() = SkyBlockItemsRepo.getItemStack(sack)
    }
}
