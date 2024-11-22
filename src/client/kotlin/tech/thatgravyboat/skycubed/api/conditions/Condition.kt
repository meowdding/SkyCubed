package tech.thatgravyboat.skycubed.api.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult

interface Condition {

    val id: String

    fun test(): Boolean

    companion object {

        val CODEC: Codec<Condition> = Codec.STRING.partialDispatch(
            "condition",
            { DataResult.success(it.id) },
            { id ->
                when (id) {
                    "island" -> DataResult.success(IslandCondition.CODEC)
                    "season" -> DataResult.success(SeasonCondition.CODEC)
                    else -> DataResult.error { "Unknown condition: $id" }
                }
            }
        )
    }
}