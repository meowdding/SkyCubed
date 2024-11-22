package tech.thatgravyboat.skycubed.api.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import net.minecraft.core.BlockPos
import tech.thatgravyboat.skycubed.SkyCubed

typealias LocationCondition = (BlockPos) -> Boolean

object LocationConditions {

    private val regex = Regex("(?<variable>[x-z])(?<operator>[<>])(?<value>-?\\d+)")
    val CODEC = Codec.STRING.listOf().flatComapMap(LocationConditions::of) { DataResult.error { "Can't be encoded" } }

    val TRUE = { _: BlockPos -> true }
    val FALSE = { _: BlockPos -> false }

    private fun of(conditionStrings: List<String>): LocationCondition {
        val conditions = conditionStrings.mapNotNull { of(it) }
        if (conditions.isEmpty()) {
            return TRUE
        } else if (conditions.size == 1) {
            return conditions.first()
        } else {
            return { pos ->
                conditions.any { it(pos) }
            }
        }
    }

    private fun of(condition: String): LocationCondition? {
        val parsedConditions = condition.lowercase()
            .replace(" ", "")
            .split("&&")
            .map(String::trim)
            .map(regex::matchEntire)

        if (parsedConditions.any { it == null }) {
            SkyCubed.logger.warn("Invalid location condition: $condition")
            return null
        }

        val conditions: List<LocationCondition> = parsedConditions
            .mapNotNull { it?.destructured }
            .mapNotNull { (variable, operator, value) ->
                val valueInt = value.toInt()
                val variableGetter = when (variable) {
                    "x" -> { pos: BlockPos -> pos.x }
                    "y" -> { pos: BlockPos -> pos.y }
                    "z" -> { pos: BlockPos -> pos.z }
                    else -> {
                        SkyCubed.logger.warn("Invalid variable: $variable")
                        null
                    }
                } ?: return null

                when (operator) {
                    "<" -> { pos -> variableGetter(pos) < valueInt }
                    ">" -> { pos -> variableGetter(pos) > valueInt }
                    else -> {
                        SkyCubed.logger.warn("Invalid operator: $operator")
                        null
                    }
                }
            }

        if (conditions.isEmpty()) {
            SkyCubed.logger.warn("Invalid location condition: $condition")
            return null
        } else if (conditions.size == 1) {
            return conditions.first()
        } else {
            return { pos -> conditions.all { it(pos) } }
        }
    }

}