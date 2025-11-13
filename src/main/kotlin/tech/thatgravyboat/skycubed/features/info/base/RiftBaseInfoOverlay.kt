package tech.thatgravyboat.skycubed.features.info.base

import tech.thatgravyboat.skyblockapi.api.area.rift.RiftAPI
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

object RiftBaseInfoOverlay : InfoDisplayOverride(SkyBlockIsland.THE_RIFT) {
    private val clockIcon = icon("rift/clock")
    private val pausedIcon = icon("rift/paused")

    private val pausedRiftTimeAreas = setOf(
        SkyBlockAreas.WIZARD_TOWER,
        SkyBlockAreas.RIFT_GALLERY,
        SkyBlockAreas.RIFT_GALLERY_ENTRANCE,
        SkyBlockAreas.MIRRORVERSE,
    )

    private fun isTimePaused(): Boolean = LocationAPI.area in pausedRiftTimeAreas

    override fun getIcon() = if (isTimePaused()) pausedIcon else clockIcon
    override fun getText() = RiftAPI.time?.let { toBeautiful(it) } ?: "0s"
    override fun getTextColor() = if (isTimePaused()) 0xAAAAAAu else 0x55FF55u
}
