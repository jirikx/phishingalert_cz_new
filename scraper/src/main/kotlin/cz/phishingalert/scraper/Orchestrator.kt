package cz.phishingalert.scraper

import cz.phishingalert.scraper.configuration.AppConfig
import cz.phishingalert.scraper.crawler.PlaywrightCrawler
import cz.phishingalert.scraper.downloaders.DnsDownloader
import cz.phishingalert.scraper.downloaders.ModuleDownloader
import cz.phishingalert.scraper.downloaders.WebsiteDownloader
import org.springframework.stereotype.Component
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createTempDirectory

@Component
class Orchestrator(
    private val appConfig: AppConfig,
    private val websiteDownloader: WebsiteDownloader,
    private val dnsDownloader: DnsDownloader,
    private val moduleDownloader: ModuleDownloader,
    private val crawler: PlaywrightCrawler
    //private val exporter: String
) {
    fun scrape(rawUrl: String): Unit {
        //todo: validate domain
        val url = URL(rawUrl)

        val dir = setupDownloadDirectory()
        println("Created tmp directory in ${dir.toUri()}")

//        websiteDownloader.makeWhoIsRequest(url)
//        websiteDownloader.download
//        dnsDownloader.download(url)
//        moduleDownloader.download(url)

        crawler.crawl(url, dir)
    }

    fun checkScrapingTimeout(webDomain: String): Boolean {
        val lastTimeOfDomainScraping: Int = TODO()
        val timeNow: Int = TODO()

        return (timeNow - lastTimeOfDomainScraping) > appConfig.timeLimit
    }

    fun setupDownloadDirectory(): Path = createTempDirectory("phishing-alert-")
}