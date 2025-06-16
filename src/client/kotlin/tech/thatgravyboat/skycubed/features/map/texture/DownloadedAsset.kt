package tech.thatgravyboat.skycubed.features.map.texture

import com.google.common.hash.Hashing
import net.minecraft.Util
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skycubed.SkyCubed
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

object DownloadedAsset {

    private val ALLOWED_DOMAINS: Set<String> = setOf(
        "teamresourceful.com",
        "files.teamresourceful.com",
        "raw.githubusercontent.com",
        "femboy-hooters.net",
        "api.dediamondpro.dev"
    )

    private val CLIENT: HttpClient = HttpClient
        .newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    fun runDownload(uri: String?, file: File, callback: () -> Unit): CompletableFuture<Void> {
        return CompletableFuture.runAsync(
            {
                createUrl(uri)?.runCatching {
                    val request = HttpRequest.newBuilder(this)
                        .GET()
                        .build()

                    val response = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream())
                    if (response.statusCode() in 200..299) {
                        FileUtils.copyInputStreamToFile(response.body(), file)

                        McClient.tell { callback() }
                    }
                }?.onFailure { exception ->
                    SkyCubed.error("Failed to download asset from URI: $uri", exception)
                }
            },
            Util.backgroundExecutor(),
        )
    }

    @Suppress("DEPRECATION")
    fun getUrlHash(url: String?): String {
        val hashedUrl = FilenameUtils.getBaseName(url)
        return Hashing.sha1().hashUnencodedChars(hashedUrl).toString()
    }

    private fun createUrl(string: String?): URI? = string?.runCatching {
        val url = URI.create(this)
        if (!ALLOWED_DOMAINS.contains(url.host)) error("Tried to load texture from disallowed domain: ${url.host}")
        if (url.scheme != "https") error("Invalid scheme")
        url
    }?.onFailure { exception ->
        SkyCubed.error("Failed to create URI from string: $string", exception)
    }?.getOrNull()
}
