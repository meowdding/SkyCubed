package tech.thatgravyboat.skycubed.features.map.texture

import com.google.common.hash.Hashing
import com.teamresourceful.resourcefullib.common.lib.Constants
import net.minecraft.Util
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

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

    fun runDownload(uri: String?, file: File, callback: Consumer<InputStream?>): CompletableFuture<Void> {
        return CompletableFuture.runAsync({
            createUrl(uri).ifPresent { url: URI? ->
                try {
                    val request = HttpRequest.newBuilder(url)
                        .GET()
                        .build()

                    val response =
                        CLIENT.send(
                            request,
                            HttpResponse.BodyHandlers.ofInputStream()
                        )
                    if (response.statusCode() / 100 != 2) return@ifPresent
                    FileUtils.copyInputStreamToFile(response.body(), file)

                    McClient.tell {
                        runCatching {
                            FileUtils.openInputStream(file).use { stream ->
                                callback.accept(stream)
                            }
                        }
                    }
                } catch (_: IOException) {
                } catch (_: InterruptedException) {
                }
            }
        }, Util.backgroundExecutor())
    }

    @Suppress("DEPRECATION")
    fun getUrlHash(url: String?): String {
        val hashedUrl = FilenameUtils.getBaseName(url)
        return Hashing.sha1().hashUnencodedChars(hashedUrl).toString()
    }

    private fun createUrl(string: String?): Optional<URI> {
        if (string == null) return Optional.empty()
        try {
            val url = URI.create(string)
            if (!ALLOWED_DOMAINS.contains(url.host)) {
                Constants.LOGGER.warn("Tried to load texture from disallowed domain: {}", url.host)
                return Optional.empty()
            }
            if (url.scheme != "https") return Optional.empty()
            return Optional.of(url)
        } catch (ignored: Exception) {
            return Optional.empty()
        }
    }
}