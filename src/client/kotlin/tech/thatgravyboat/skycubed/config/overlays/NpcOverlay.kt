package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Range
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Slider

@ConfigObject
class NpcOverlay(
    @ConfigEntry(id = "enabled") var enabled: Boolean = false,
    @ConfigEntry(id = "durationPerMessage") var durationPerMessage: Float = 2.5f,
    @ConfigEntry(id = "durationForActionMessage") var durationForActionMessage: Float = 10f,
    @ConfigEntry(id = "radius") @Range(min = 0.0, max = 15.0) @Slider var overlayRadius: Float = 0f,
)