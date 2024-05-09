package cz.phishingalert.scraper

import com.microsoft.playwright.Playwright
import cz.phishingalert.common.domain.ModuleInfo
import cz.phishingalert.common.messagequeue.ScrapingMessage
import cz.phishingalert.scraper.configuration.AppConfig
import cz.phishingalert.scraper.configuration.PlaywrightConfig
import cz.phishingalert.scraper.crawler.playwright.PlaywrightCrawler
import cz.phishingalert.scraper.downloaders.CertificateDownloader
import cz.phishingalert.scraper.downloaders.DnsDownloader
import cz.phishingalert.scraper.downloaders.ModuleDownloader
import cz.phishingalert.scraper.downloaders.WebsiteDownloader
import cz.phishingalert.scraper.exporters.models.ModelExporter
import cz.phishingalert.scraper.exporters.SftpExporter
import cz.phishingalert.scraper.utils.checkURL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.name

@Component
class Orchestrator(
    private val appConfig: AppConfig,
    private val websiteDownloader: WebsiteDownloader,
    private val dnsDownloader: DnsDownloader,
    private val certificateDownloader: CertificateDownloader,
    private val playwrightConfig: PlaywrightConfig,
    private val exporter: ModelExporter
) {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Get data about website with given [rawUrl] and connect it with the phishing accident from [scrapingMessage]
     * The website is connected with phishing accident only if the optional argument [scrapingMessage] is specified
     */
    fun scrape(rawUrl: String, scrapingMessage: ScrapingMessage? = null) {
       if (!checkURL(rawUrl, true)) {
           logger.error("Can't scrape info from invalid url $rawUrl")
           return
       }
        val url = URL(rawUrl)

        // Setup directory for downloading
        val dir = setupDownloadDirectory(scrapingMessage?.accidentId)
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
            var usedModules = emptyList<ModuleInfo>()
            moduleDownloader.use {
                usedModules = it.download(url)
            }

            // Crawl the website
            val crawler = PlaywrightCrawler(playwright, playwrightConfig.options(), appConfig.crawlerConfig)
            crawler.crawl(url, dir)

            // Export the results
            if (scrapingMessage == null) {
                websiteInfo.first().fileSystemPath = dir.toString()
                exporter.export(websiteInfo.first(), dnsRecords, usedModules, certs)
            } else {
                websiteInfo.first().fileSystemPath = File(scrapingMessage.crawledDataPath).resolve(dir.name).toString()
                exporter.export(scrapingMessage.accidentId, websiteInfo.first(), dnsRecords, usedModules, certs)

                // Transfer crawled data to the Core
                val sftpExporter = SftpExporter(appConfig.sftpConfig)
                sftpExporter.use {
                    sftpExporter.sendDataFromDir(dir.toString(), scrapingMessage.crawledDataPath)
                }
            }
        }
    }

    fun setupDownloadDirectory(id: Int?): Path = createTempDirectory("phishing-alert-$id-")
}