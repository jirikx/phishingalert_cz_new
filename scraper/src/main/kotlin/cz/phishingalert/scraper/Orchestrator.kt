package cz.phishingalert.scraper

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import cz.phishingalert.scraper.configuration.AppConfig
import cz.phishingalert.scraper.configuration.PlaywrightConfig
import cz.phishingalert.scraper.crawler.playwright.PlaywrightCrawler
import cz.phishingalert.scraper.downloaders.CertificateDownloader
import cz.phishingalert.scraper.downloaders.DnsDownloader
import cz.phishingalert.scraper.downloaders.ModuleDownloader
import cz.phishingalert.scraper.downloaders.WebsiteDownloader
import cz.phishingalert.scraper.exporters.Exporter
import cz.phishingalert.scraper.utils.checkURL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.math.exp

@Component
class Orchestrator(
    private val appConfig: AppConfig,
    private val websiteDownloader: WebsiteDownloader,
    private val dnsDownloader: DnsDownloader,
    private val certificateDownloader: CertificateDownloader,
    private val playwrightConfig: PlaywrightConfig,
    private val exporter: Exporter
) {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Get data about website with given [rawUrl] and connect it with the phishing accident identified by [accidentId]
     * The website is connected with phishing accident only if the optional argument [accidentId] is specified
     */
    fun scrape(rawUrl: String, accidentId: Int? = null) {
       if (!checkURL(rawUrl, true)) {
           logger.error("Can't scrape info from invalid url $rawUrl")
           return
       }
        val url = URL(rawUrl)

        // Setup directory for downloading
        val dir = setupDownloadDirectory()
        logger.info("Created tmp directory in ${dir.toUri()}")

        // Download various website data
        val websiteInfo = websiteDownloader.download(url)
        val dnsRecords = dnsDownloader.download(url)
        val certs = certificateDownloader.download(url)

        // Get data which depend on Playwright
        val playwrightInstance = Playwright.create()
        playwrightInstance.use { playwright ->
            // Download modules info
            val moduleDownloader = ModuleDownloader(playwright)
            val usedModules = moduleDownloader.download(url)

            // Crawl the website
            val crawler = PlaywrightCrawler(playwright, playwrightConfig.options(), appConfig.crawlerConfig)
            crawler.crawl(url, dir)

            // Export the results
            if (accidentId == null)
                exporter.export(websiteInfo.first(), dnsRecords, usedModules, certs)
            else
                exporter.export(accidentId, websiteInfo.first(), dnsRecords, usedModules, certs)
        }
    }

    fun checkScrapingTimeout(webDomain: String): Boolean {
        val lastTimeOfDomainScraping: Int = TODO()
        val timeNow: Int = TODO()

        return (timeNow - lastTimeOfDomainScraping) > appConfig.timeLimit
    }

    fun setupDownloadDirectory(): Path = createTempDirectory("phishing-alert-")
}