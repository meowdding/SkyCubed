package tech.thatgravyboat.skycubed.features.overlays.commissions

import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import me.owdding.lib.events.FinishRepoLoadingEvent
import me.owdding.repo.RemoteRepo
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.area.mining.CommissionArea
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skycubed.config.overlays.CommissionOverlayConfig

@Module
object CommissionFormatters {

    private val formatters: MutableMap<String, MutableMap<String, CommissionFormatter>> = mutableMapOf()

    @Subscription(FinishRepoLoadingEvent::class)
    private fun loadRepo() {
        val file = RemoteRepo.getFileContentAsJson("skycubed/commissions")
        file.toData(Codec.unboundedMap(Codec.STRING, Codec.unboundedMap(Codec.STRING, CommissionFormatter.CODEC)))
            ?.let(formatters::putAll)
    }

    fun format(name: String, decimal: Float): Component {
        if (!CommissionOverlayConfig.format) return PercentCommissionFormatter.format(decimal)
        val area = CommissionArea.entries.firstOrNull { it.areaCheck() } ?: return PercentCommissionFormatter.format(decimal)
        return formatters[area.name]?.get(name)?.format(decimal) ?: PercentCommissionFormatter.format(decimal)
    }
}
