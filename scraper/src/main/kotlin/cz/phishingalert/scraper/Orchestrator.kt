package cz.phishingalert.scraper

import cz.phishingalert.scraper.downloaders.DnsDownloader
import cz.phishingalert.scraper.downloaders.ModuleDownloader
import cz.phishingalert.scraper.downloaders.WebsiteDownloader
import org.springframework.stereotype.Component
import java.net.URL

@Component
class Orchestrator(
    private val appConfig: AppConfig,
    private val websiteDownloader: WebsiteDownloader,
    private val dnsDownloader: DnsDownloader,
    private val moduleDownloader: ModuleDownloader
    //private val exporter: String
) {
    fun scrape(rawUrl: String): Unit {
        //todo: validate domain
        val url = URL(rawUrl)

        //websiteDownloader.makeWhoIsRequest(url)
        //websiteDownloader.download
        dnsDownloader.download(url)
        moduleDownloader.download(url)
    }

    fun checkScrapingTimeout(webDomain: String): Boolean {
        val lastTimeOfDomainScraping: Int = TODO()
        val timeNow: Int = TODO()

        return (timeNow - lastTimeOfDomainScraping) > appConfig.timeLimit
    }
}