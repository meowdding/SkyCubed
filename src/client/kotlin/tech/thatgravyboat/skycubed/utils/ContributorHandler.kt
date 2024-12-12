package tech.thatgravyboat.skycubed.utils

import kotlinx.coroutines.runBlocking
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import java.util.*

object ContributorHandler {
    var contributors: Map<UUID, ContributorData> = emptyMap()
        private set

    init {
        runBlocking {
            try {
                val data = this.javaClass.getResourceAsStream("/repo/contributors.json")
                    ?.readJson<Map<UUID, ContributorData>>() ?: return@runBlocking
                contributors = data
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}

data class ContributorData(
    val symbol: String,
)
