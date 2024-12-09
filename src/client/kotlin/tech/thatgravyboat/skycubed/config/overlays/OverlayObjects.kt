package tech.thatgravyboat.skycubed.config.overlays

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigObject
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Range
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption.Slider
import tech.thatgravyboat.skycubed.features.overlays.pickuplog.PickUpLogComponents

@ConfigObject
class NpcOverlay(
    @ConfigEntry(id = "enabled") var enabled: Boolean = false,
    @ConfigEntry(id = "durationPerMessage") var durationPerMessage: Float = 2.5f,
    @ConfigEntry(id = "durationForActionMessage") var durationForActionMessage: Float = 10f,
    @ConfigEntry(id = "radius") @Range(min = 0.0, max = 15.0) @Slider var overlayRadius: Float = 0f,
)

@ConfigObject
class CommissionOverlay(
    @ConfigEntry(id = "enabled") var enabled: Boolean = true,
    @ConfigEntry(id = "format") var format: Boolean = true,
    @ConfigEntry(id = "background") var background: Boolean = false,
)

@ConfigObject
class PickupLogOverlay(
    @ConfigEntry(id = "enabled") var enabled: Boolean = true,
    @ConfigEntry(id = "compact") var compact: Boolean = false,
    @ConfigEntry(id = "appearance")
    @ConfigOption.Draggable
    var appearance: Array<PickUpLogComponents> = PickUpLogComponents.entries.toTypedArray(),
)

@ConfigObject
class MapOverlay(
    @ConfigEntry(id = "enabled") var enabled: Boolean = false,
)