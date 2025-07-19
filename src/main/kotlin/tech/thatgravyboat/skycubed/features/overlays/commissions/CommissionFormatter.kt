package tech.thatgravyboat.skycubed.features.overlays.commissions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.text.DecimalFormat

interface CommissionFormatter {
    fun format(decimal: Float): Component

    companion object {

        val CODEC: Codec<CommissionFormatter> = Codec.STRING.dispatch(
            "type",
            { formatter: CommissionFormatter ->
                when (formatter) {
                    is BooleanCommissionFormatter -> "boolean"
                    is PercentCommissionFormatter -> "percent"
                    is DecimalCommissionFormatter -> "decimal"
                    else -> throw IllegalArgumentException("Invalid formatter type: $formatter")
                }
            }, { id: String ->
                when (id) {
                    "boolean" -> BooleanCommissionFormatter.CODEC
                    "percent" -> PercentCommissionFormatter.CODEC
                    "decimal" -> DecimalCommissionFormatter.CODEC
                    else -> throw IllegalArgumentException("Invalid formatter type: $id")
                }
            }
        )

        fun getColor(percent: Float): Int = when {
            percent < 0.3 -> TextColor.RED
            percent < 0.5 -> TextColor.GOLD
            percent < 1 -> TextColor.YELLOW
            else -> TextColor.GREEN
        }
    }
}

object BooleanCommissionFormatter : CommissionFormatter {

    val CODEC: MapCodec<BooleanCommissionFormatter> = MapCodec.unit { BooleanCommissionFormatter }

    override fun format(decimal: Float): Component {
        return Text.of(if (decimal > 0) "✔" else "❌") {
            this.color = CommissionFormatter.getColor(decimal)
        }
    }
}

object PercentCommissionFormatter : CommissionFormatter {

    val CODEC: MapCodec<PercentCommissionFormatter> = MapCodec.unit { PercentCommissionFormatter }

    private val formatter = DecimalFormat("##.##%")

    override fun format(decimal: Float): Component {
        return Text.of(formatter.format(decimal)) {
            this.color = CommissionFormatter.getColor(decimal)
        }
    }
}

data class DecimalCommissionFormatter(private val max: Int) : CommissionFormatter {

    override fun format(decimal: Float): Component {
        val current: Int = (decimal * max).toInt()
        return Text.of("$current/$max") {
            this.color = CommissionFormatter.getColor(decimal)
        }
    }

    companion object {
        val CODEC: MapCodec<DecimalCommissionFormatter> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.INT.fieldOf("max").forGetter(DecimalCommissionFormatter::max)
            ).apply(it, ::DecimalCommissionFormatter)
        }
    }
}