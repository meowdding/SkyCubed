package tech.thatgravyboat.skycubed.features.overlays.mining.commissions

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import kotlinx.coroutines.runBlocking
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionArea
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skycubed.SkyCubed
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig

object CommissionFormatters {

    private val formatters: MutableMap<String, MutableMap<String, CommissionFormatter>> = mutableMapOf()

    init {
        runBlocking {
            runCatching {
                val file = SkyCubed.loadFromRepo<JsonObject>("commissions")
                file.toData(Codec.unboundedMap(Codec.STRING, Codec.unboundedMap(Codec.STRING, CommissionFormatter.CODEC)))
                    ?.let(formatters::putAll)
            }
        }
    }

    fun format(name: String, decimal: Float): Component {
        if (!OverlaysConfig.commissions.format) return PercentCommissionFormatter.format(decimal)
        val area = CommissionArea.entries.firstOrNull { it.areaCheck() } ?: return PercentCommissionFormatter.format(decimal)
        return formatters[area.name]?.get(name)?.format(decimal) ?: PercentCommissionFormatter.format(decimal)
    }
}