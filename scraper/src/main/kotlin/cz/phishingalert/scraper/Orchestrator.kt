package cz.phishingalert.scraper

import cz.phishingalert.scraper.configuration.AppConfig
import cz.phishingalert.scraper.crawler.PlaywrightCrawler
import cz.phishingalert.scraper.downloaders.CertificateDownloader
import cz.phishingalert.scraper.downloaders.DnsDownloader
import cz.phishingalert.scraper.downloaders.ModuleDownloader
import cz.phishingalert.scraper.downloaders.WebsiteDownloader
import cz.phishingalert.scraper.downloaders.parsers.WebsiteInfoParser
import cz.phishingalert.scraper.utils.checkURL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

@Component
class Orchestrator(
    private val appConfig: AppConfig,
    private val websiteDownloader: WebsiteDownloader,
    private val dnsDownloader: DnsDownloader,
    private val moduleDownloader: ModuleDownloader,
    private val crawler: PlaywrightCrawler,
    private val certificateDownloader: CertificateDownloader
    //private val exporter: String
) {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun scrape(rawUrl: String): Unit {
        //todo: validate domain
       if (!checkURL(rawUrl, true)) {
           logger.error("Can't scrape info from invalid url $rawUrl")
           return
       }

        val url = URL(rawUrl)

//        val dir = setupDownloadDirectory()
//        logger.info("Created tmp directory in ${dir.toUri()}")
//
        println(websiteDownloader.makeWhoIsRequest(url.host))
        println(websiteDownloader.makeRDAPRequest(url.host))
        return

//        websiteDownloader.makeRDAPRequest(url.host)
//        dnsDownloader.download(url)
//        moduleDownloader.download(url)
//
//        crawler.crawl(url, dir)
//        certificateDownloader.download(url)
    }

    fun checkScrapingTimeout(webDomain: String): Boolean {
        val lastTimeOfDomainScraping: Int = TODO()
        val timeNow: Int = TODO()

        return (timeNow - lastTimeOfDomainScraping) > appConfig.timeLimit
    }

    fun setupDownloadDirectory(): Path = createTempDirectory("phishing-alert-")
}