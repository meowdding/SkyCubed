package tech.thatgravyboat.skycubed.utils

import me.owdding.ktmodules.Module
import me.owdding.lib.cosmetics.MlibCosmetics
import me.owdding.lib.events.FinishRepoLoadingEvent
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import java.util.*

@Module
object ContributorHandler {
    var contributors: Map<UUID, ContributorData> = emptyMap()
        private set

    @Subscription
    fun onRepoLoad(event: FinishRepoLoadingEvent) {
        contributors = MlibCosmetics.mlibCosmetics.mapNotNull { it.key to ContributorData(it.value.suffix ?: return@mapNotNull null) }.toMap()
    }
}

data class ContributorData(
    val symbol: Component,
)
