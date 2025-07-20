package tech.thatgravyboat.skycubed.api.repo

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.skycubed.generated.SkyCubedCodecs
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skycubed.SkyCubed

@Module
object SackCodecs {
    val data: List<Sack> by lazy { SkyCubed.loadRepoData("sacks", SkyCubedCodecs.getCodec<Sack>().listOf()) }
    val sackItems by lazy { data.flatMap { it.items }.map { it to RepoItemsAPI.getItem(it) }.sortedBy { (_, v) -> v.cleanName }.toMap() }

    @GenerateCodec
    data class Sack(
        val sack: String,
        val items: List<String>,
    ) {
        val item by lazy { RepoItemsAPI.getItem(sack) }
    }
}
