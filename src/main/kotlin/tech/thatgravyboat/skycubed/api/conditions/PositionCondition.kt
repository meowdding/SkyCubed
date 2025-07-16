package tech.thatgravyboat.skycubed.api.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skycubed.api.PositionPredicates
import tech.thatgravyboat.skycubed.utils.Codecs.asMapCodec

data class PositionCondition(private val position: String) : Condition {

    private val condition by lazy { PositionPredicates.of(position) }

    override val id: String = "position"
    override fun test(): Boolean = condition?.invoke(McPlayer.self?.blockPosition() ?: BlockPos.ZERO) ?: false

    companion object {
        val CODEC: MapCodec<PositionCondition> = Codec.STRING
            .xmap(::PositionCondition, PositionCondition::position)
            .asMapCodec("position")
    }
}