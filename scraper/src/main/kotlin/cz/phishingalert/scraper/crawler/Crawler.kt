package cz.phishingalert.scraper.crawler

import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Path

abstract class Crawler {
    /**
     * Crawl the website starting at given url
     */
    abstract fun crawl(url: URL, downloadDir: Path)

    /**
     * Download files from the given set of URIs and store them into the directory
     */
    fun downloadFromUris(uris: Set<URI>, downloadDir: Path) {
        for (uri in uris) {
            val resourceUrl = uri.toURL()
            val fileName = resourceUrl.path.split('/').last()


            // Download the file from resourceUrl into downloadDir
            try {
                resourceUrl.openStream().use {
                    Channels.newChannel(it).use { urlChannel ->
                        FileOutputStream(downloadDir.resolve(fileName).toString()).use { fileOutputStream ->
                            fileOutputStream.channel.transferFrom(urlChannel, 0, Long.MAX_VALUE)
                        }
                    }
                }
            } catch (ex: IOException) {
                println(ex.message) //todo: implement logging
            }
        }

    }

    fun fixUri(url: String, rawUrl: String): URI? {
        val fixed = when {
            rawUrl.startsWith("http") -> rawUrl
            rawUrl.startsWith("//") -> "https:$rawUrl"
            rawUrl.startsWith("/") -> "$url$rawUrl"
            else -> "$url/$rawUrl"
        }

        return try {
            URI(fixed)
        } catch (ex: URISyntaxException) {
            null
        }
    }

    /**
     * Parse the raw URIs and turn them into correct "clickable" format
     */
    fun fixUris(url: String, rawUrls: Set<String>): Set<URI> {
        val result = HashSet<URI>()

        for (rawUrl in rawUrls) {
            val fixed = fixUri(url, rawUrl)

            // The crawled page might contain a link with syntax error, in that case it's skipped
            if (fixed != null)
                result.add(fixed)
            else
                println("Website $url contains wrong link $rawUrl")
        }

        return result
    }
}