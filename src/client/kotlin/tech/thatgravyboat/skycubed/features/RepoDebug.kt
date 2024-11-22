package tech.thatgravyboat.skycubed.features

import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object RepoDebug {

    private val idCache: MutableSet<String> = mutableSetOf()
    private val uuidCache: MutableSet<String> = mutableSetOf()
    private var failedToLoad = false

    private fun loadCache() {
        runBlocking {
            failedToLoad = runCatching {
                val result = Http.getResult<JsonObject>("https://tgb-76d1f429.teamresourceful.com/items", errorFactory = ::RuntimeException)
                    .getOrThrow()
                result.get("id").asJsonArray.forEach { id -> idCache.add(id.asString) }
                result.get("uuids").asJsonArray.forEach { uuid -> uuidCache.add(uuid.asString) }
            }.getOrNull() == null
        }
    }

    init {
        Scheduling.schedule(0.seconds, 20.minutes) {
            loadCache()
        }
    }

    private fun sendToServer(stack: ItemStack) {
        val uuid = stack.get(DataComponents.CUSTOM_DATA)?.unsafe?.get("uuid")?.asString
        val id = stack.getData(DataTypes.ID) ?: return
        if (uuid == null && id in idCache) return
        if (uuid != null && uuid in uuidCache) return
        idCache.add(id)
        uuid?.let { uuidCache.add(it) }

        CompletableFuture.runAsync {
            runBlocking {
                runCatching {
                    Http.post(
                        "https://tgb-76d1f429.teamresourceful.com/add/${id}",
                        headers = mapOf("Content-Type" to "application/json"),
                        body = stack.toJson(ItemStack.CODEC).toString()
                    ) {
                        if (this.statusCode != 200) {
                            println("Failed to send item to server: ${this.statusCode}")
                        }
                    }
                }
            }
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onInventoryLoad(event: ContainerInitializedEvent) {
        if (failedToLoad) return
        event.itemStacks.forEach(::sendToServer)
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onItemStackChange(event: ContainerChangeEvent) {
        if (failedToLoad) return
        sendToServer(event.item)
    }
}