package tech.thatgravyboat.skycubed.api.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import tech.thatgravyboat.skycubed.utils.Codecs.asMapCodec

data class NotCondition(private val condition: Condition) : Condition {
    override val id: String = "not"
    override fun test(): Boolean = !condition.test()

    companion object {
        val CODEC: MapCodec<NotCondition> = Codec.lazyInitialized {
            Condition.CODEC.xmap(::NotCondition, NotCondition::condition)
        }.asMapCodec("value")
    }
}

data class OrCondition(private val conditions: List<Condition>) : Condition {
    override val id: String = "or"
    override fun test(): Boolean = conditions.any(Condition::test)

    companion object {
        val CODEC: MapCodec<OrCondition> = Codec.lazyInitialized {
            Condition.CODEC.listOf().xmap(::OrCondition, OrCondition::conditions)
        }.asMapCodec("conditions")
    }
}

data class AndCondition(private val conditions: List<Condition>) : Condition {
    override val id: String = "and"
    override fun test(): Boolean = conditions.any(Condition::test)

    companion object {
        val CODEC: MapCodec<AndCondition> = Codec.lazyInitialized {
            Condition.CODEC.listOf().xmap(::AndCondition, AndCondition::conditions)
        }.asMapCodec("conditions")
    }
}