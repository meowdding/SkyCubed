package tech.thatgravyboat.skycubed.features.info.base

import tech.thatgravyboat.skyblockapi.api.area.dungeon.DungeonAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

object DungeonBaseInfoOverlay : InfoDisplayOverride(SkyBlockIsland.THE_CATACOMBS) {
    private val dungeonFloorIcon = listOf(
        icon("dungeons/entrance"),
        icon("dungeons/bonzo"),
        icon("dungeons/scarf"),
        icon("dungeons/professor"),
        icon("dungeons/thorn"),
        icon("dungeons/livid"),
        icon("dungeons/sadan"),
        icon("dungeons/wither"),
    )
    private val clockIcon = icon("rift/clock")

    override fun getIcon() = DungeonAPI.dungeonFloor?.floorNumber?.let { dungeonFloorIcon[it] } ?: clockIcon
    override fun getText() = toBeautiful(DungeonAPI.time)
    override fun getTextColor() = 0x55FF55u
}
