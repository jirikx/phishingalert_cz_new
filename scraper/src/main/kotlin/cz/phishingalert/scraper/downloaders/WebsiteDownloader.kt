package cz.phishingalert.scraper.downloaders

import crawlercommons.filters.basic.BasicURLNormalizer
import cz.phishingalert.scraper.AppConfig
import cz.phishingalert.scraper.checkURL
import de.hshn.mi.crawler4j.frontier.HSQLDBFrontierConfiguration
import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import org.apache.commons.net.whois.WhoisClient
import org.springframework.stereotype.Component
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration

private const val WHOIS_PORT = 43
private const val MAX_REDIRECT_COUNT = 3
private const val LOCAL_DOWNLOAD_PATH = "/tmp/crawled-content/" //todo: move to config file!

@Component
class WebsiteDownloader(val appConfig: AppConfig) : Downloader {

    override fun download(domain: String) {
        // find a location where to store the page
        // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io.path/create-temp-file.html
        println("Storing to: ${appConfig.downloaderConfig.filePath}")


        // Where to store crawled data
        val storageFolder = File(LOCAL_DOWNLOAD_PATH)
        if (!storageFolder.exists())
            storageFolder.mkdirs()

        // Use the crawler
        val numberOfCrawlers = 2
        val config = CrawlConfig()
        config.crawlStorageFolder = LOCAL_DOWNLOAD_PATH
        config.maxPagesToFetch = 50
        config.isIncludeBinaryContentInCrawling = true
        config.userAgentString = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:123.0) Gecko/20100101 Firefox/123.0"

        // Instantiate the controller
        val normalizer = BasicURLNormalizer.newBuilder().idnNormalization(BasicURLNormalizer.IdnNormalization.NONE).build()
        val pageFetcher = PageFetcher(config, normalizer)
        val robotsTxtConfig = RobotstxtConfig()
        val frontierConfiguration = HSQLDBFrontierConfiguration(config, 10)
        val robotsTxtServer = RobotstxtServer(robotsTxtConfig, pageFetcher, frontierConfiguration.webURLFactory)
        val controller = CrawlController(config, normalizer, pageFetcher, robotsTxtServer, frontierConfiguration)



        // Add seeds for crawler
        controller.addSeed("https://unblock.pancakeswaapfinance.net/")
        val factory: WebCrawlerFactory<Crawler> = WebCrawlerFactory<Crawler> { Crawler(storageFolder) }


        controller.start(factory, numberOfCrawlers)

    }

    fun makeWhoIsRequest(domain: String): Boolean {
        var currentServer: String? = "whois.iana.org"
        var previousServer: String? = "whois.iana.org"
        val ianaRegex = Regex("refer:\\s+(.*)", RegexOption.MULTILINE)  // match the corresponding server for the TLD
        val commonRegex = Regex("registrar WHOIS server: (.*?)\\s", RegexOption.IGNORE_CASE) // match pointers to next servers

        val whoIsClient = WhoisClient()
        whoIsClient.connect(currentServer, WHOIS_PORT)

        // Query the IANA to get the server for given TLD
        var results = whoIsClient.query(domain).trimIndent()
        currentServer = ianaRegex.find(results)?.groupValues?.get(1)
        if (currentServer == null || !checkURL(currentServer)) {
            whoIsClient.disconnect()
            return false
        }

        // Make requests to referenced servers until we get to the final one
        var redirectCount = 0
        while (currentServer != previousServer && redirectCount++ < MAX_REDIRECT_COUNT) {
            previousServer = currentServer
            whoIsClient.connect(currentServer, WHOIS_PORT)
            results = whoIsClient.query(domain).trimIndent()
            currentServer = commonRegex.find(results)?.groupValues?.get(1)

            if (currentServer == null || !checkURL(currentServer))
                break
        }

        whoIsClient.disconnect()
        println(results)
        return true
    }

    fun makeRDAPRequest(domain: String): Boolean {
        val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS).build()
        val request = HttpRequest.newBuilder()
            .uri(URI("https://rdap.org/domain/${domain}"))
            .timeout(Duration.ofSeconds(10)).GET().build()

        val response = client.send(request, BodyHandlers.ofString())
        println(response.body())
        return true
    }
}