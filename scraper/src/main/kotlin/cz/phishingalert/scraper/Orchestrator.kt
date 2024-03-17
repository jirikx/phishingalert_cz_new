package cz.phishingalert.scraper

import cz.phishingalert.scraper.downloaders.WebsiteDownloader
import org.springframework.stereotype.Component

@Component
class Orchestrator(
    private val appConfig: AppConfig,
    private val websiteDownloader: WebsiteDownloader,
    //private val exporter: String
) {
    fun scrape(webDomain: String): Unit {
        //todo: validate domain

        websiteDownloader.makeWhoIsRequest(webDomain)
    }

    fun checkScrapingTimeout(webDomain: String): Boolean {
        val lastTimeOfDomainScraping: Int = TODO()
        val timeNow: Int = TODO()

        return (timeNow - lastTimeOfDomainScraping) > appConfig.timeLimit
    }
}