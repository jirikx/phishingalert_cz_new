package cz.phishingalert.scraper.crawler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Path

abstract class Crawler {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Crawl the website starting at given [originalUrl] and store the results into the [downloadDir]
     */
    abstract fun crawl(originalUrl: URL, downloadDir: Path)

    /**
     * Download files from the given set of URIs ([uris]) and store them into the [downloadDir]
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
                logger.warn("Problem when downloading from $resourceUrl, ${ex.message}")
            }
        }

    }

    /**
     * Create working URI which is combined from [url] and [rawUrl]
     */
    fun fixUri(url: String, rawUrl: String): URI? {
        // Clean the URL from # or ? tags
        var fixedUrl = url
        val tagPosition = fixedUrl.indexOfFirst { it == '#' || it == '?' }
        if (tagPosition != -1)
            fixedUrl = fixedUrl.removeRange(tagPosition, fixedUrl.length)

        val fixed = when {
            rawUrl.startsWith("http") -> rawUrl
            rawUrl.startsWith("resource:") -> return null   // Resource URLs are browser's internal matter, don't visit them
            rawUrl.startsWith("//") -> "https:$rawUrl"
            rawUrl.startsWith("/") -> "$fixedUrl$rawUrl"
            else -> "$fixedUrl/$rawUrl"
        }

        return try {
            URI(fixed)
        } catch (ex: URISyntaxException) {
            null
        }
    }

    /**
     * Parse the raw URLs and turn them into correct "clickable" format
     */
    fun fixUris(url: String, rawUrls: Set<String>): Set<URI> {
        val result = HashSet<URI>()

        for (rawUrl in rawUrls) {
            val fixed = fixUri(url, rawUrl)

            // The crawled page might contain a link with syntax error, in that case it's skipped
            if (fixed != null)
                result.add(fixed)
            else
                logger.warn("Website $url contains wrong link $rawUrl")
        }

        return result
    }
}