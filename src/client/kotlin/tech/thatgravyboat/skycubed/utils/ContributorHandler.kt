package tech.thatgravyboat.skycubed.utils

import kotlinx.coroutines.runBlocking
import tech.thatgravyboat.skycubed.SkyCubed
import java.util.*

object ContributorHandler {
    var contributors: Map<UUID, ContributorData> = emptyMap()
        private set

    init {
        runBlocking {
            try {
                contributors = SkyCubed.loadFromRepo<Map<UUID, ContributorData>>("contributors") ?: emptyMap()
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}

data class ContributorData(
    val symbol: String,
)
