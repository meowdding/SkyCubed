package tech.thatgravyboat.skycubed.features.overlays.commissions

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import kotlinx.coroutines.runBlocking
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skycubed.config.overlays.OverlaysConfig

object CommissionFormatters {

    private val formatters: MutableMap<String, CommissionFormatter> = mutableMapOf()

    init {
        runBlocking {
            runCatching {
                val file = this.javaClass.getResourceAsStream("/repo/commissions.json")?.readJson<JsonObject>() ?: return@runCatching
                file.toData(Codec.unboundedMap(Codec.STRING, CommissionFormatter.CODEC))?.let(formatters::putAll)
            }
        }
    }

    fun format(name: String, decimal: Float): Component {
        if (!OverlaysConfig.commissionsFormat) return PercentCommissionFormatter.format(decimal)
        return (formatters[name] ?: PercentCommissionFormatter).format(decimal)
    }
}