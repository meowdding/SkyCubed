package tech.thatgravyboat.skycubed.api.conditions

class ConstantCondition(
    override val id: String,
    val tester: () -> Boolean
) : Condition {
    override fun test(): Boolean = tester()
}