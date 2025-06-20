package tech.thatgravyboat.skycubed.features.dungeonmap

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

@Module
object DungeonFeatures {

    var currentInstance: DungeonInstance? = null

    @Subscription(priority = Subscription.LOWEST)
    @OnlyIn(SkyBlockIsland.THE_CATACOMBS)
    fun onEnterDungeon(event: ServerChangeEvent) {
        currentInstance = DungeonInstance(event.name)
    }

    @Subscription
    fun onExitDungeon(islandChangeEvent: IslandChangeEvent) {
        if (islandChangeEvent.old == SkyBlockIsland.THE_CATACOMBS) {
            currentInstance?.onRemove()
            currentInstance = null
        }
    }

}
