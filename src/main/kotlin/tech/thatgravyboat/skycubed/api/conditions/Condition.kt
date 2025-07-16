package tech.thatgravyboat.skycubed.api.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec


interface Condition {

    val id: String

    fun test(): Boolean

    companion object {

        val TRUE = ConstantCondition("true") { true }
        val FALSE = ConstantCondition("false") { false }

        private val conditionsCodecs = mutableMapOf(
            "true" to MapCodec.unit(TRUE),
            "false" to MapCodec.unit(FALSE),
            "not" to NotCondition.CODEC,
            "or" to OrCondition.CODEC,
            "and" to AndCondition.CODEC,
            "island" to IslandCondition.CODEC,
            "season" to SeasonCondition.CODEC,
            "position" to PositionCondition.CODEC,
            "area" to AreaCondition.CODEC,
        )

        private val dispatchedCodec = Codec.STRING.partialDispatch(
            "condition",
            { DataResult.success(it.id) },
            { id ->
                conditionsCodecs[id]?.let {
                    DataResult.success(it)
                } ?: DataResult.error {
                    "Unknown condition: $id"
                }
            }
        )

        val CODEC: Codec<Condition> = Codec.withAlternative(
            dispatchedCodec,
            Codec.BOOL.flatXmap(
                { DataResult.success(if (it) TRUE else FALSE) },
                { when (it) {
                    TRUE -> DataResult.success(true)
                    FALSE -> DataResult.success(false)
                    else -> DataResult.error { "Unable to serialize ${it.id}" }
                } }
            )
        )
    }
}