package tech.thatgravyboat.skycubed.utils

import com.google.gson.annotations.Expose
import kotlinx.coroutines.runBlocking
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import java.util.*

object ContributorHandler {
    var contributors: List<ContributorJson> = emptyList()
        private set

    init {
        runBlocking {
            try {
                val data = this.javaClass.getResourceAsStream("/repo/contributors.json")
                    ?.readJson<List<ContributorJson>>() ?: return@runBlocking
                contributors = data
            } catch (e: Exception) {
                println(e)
            }
        }
    }
}

data class ContributorJson(
    @Expose val uuid: UUID,
    @Expose val symbol: String
)