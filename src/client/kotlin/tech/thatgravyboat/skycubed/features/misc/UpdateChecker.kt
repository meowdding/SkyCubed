package tech.thatgravyboat.skycubed.features.misc

import com.google.gson.JsonArray
import kotlinx.coroutines.runBlocking
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.minecraft.SharedConstants
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.url
import kotlin.time.Duration.Companion.seconds

private const val URL = "https://api.modrinth.com/v2/project/skycubed/version"

object UpdateChecker {

    private var firstLoad = true

    @Subscription
    fun onJoinHypixel(event: IslandChangeEvent) {
        event.new ?: return
        if (!firstLoad) return
        firstLoad = false
        checkForUpdates()
    }

    private fun checkForUpdates() {
        val mcVersion = SharedConstants.getCurrentVersion().name
        val currentVersion = FabricLoader.getInstance().getModContainer("skycubed").orElseThrow().metadata.version

        runBlocking {
            Http.getResult<JsonArray>(URL).onSuccess { value ->
                val versionsForMc = value.filter { it.asJsonObject.getAsJsonArray("game_versions").any { it.asString == mcVersion }  }
                var nextVersion: Pair<String, Version>? = null

                for (versionEntry in versionsForMc) {
                    val version = runCatching {
                        Version.parse(versionEntry.asJsonObject.get("version_number").asString)
                    }.getOrNull() ?: continue

                    if (currentVersion < version && (nextVersion == null || nextVersion.second < version)) {
                        nextVersion = versionEntry.asJsonObject.get("id").asString to version
                    }
                }

                if (nextVersion != null) {
                    sendMessage(
                        "https://modrinth.com/mod/skycubed/version/${nextVersion.first}",
                        currentVersion.friendlyString,
                        nextVersion.second.friendlyString
                    )
                }
            }
        }
    }

    private fun sendMessage(link: String, current: String, new: String) {
        Scheduling.schedule(5.seconds) {
            McClient.tell {
                Text.multiline(
                    "",
                    Text.of("[SkyCubed]: New version found!").withColor(TextColor.YELLOW),
                    Text.join(
                        " ",
                        Text.of(current).withColor(TextColor.RED),
                        Text.of(" -> ").withColor(TextColor.GRAY),
                        Text.of(new).withColor(TextColor.GREEN),
                        Text.of(" Click to download.").withColor(TextColor.BLUE),
                    ).apply {
                        this.url = link
                        this.hover = Text.of(link).withColor(TextColor.GRAY)
                    },
                    "",
                ).send()
            }
        }
    }
}