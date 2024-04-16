package cz.phishingalert.scraper.crawler.playwright

import com.microsoft.playwright.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.*

class CrawlingProcess(
    private val playwright: Playwright,
    private val options: BrowserType.LaunchPersistentContextOptions,
    private val profilePath: Path,
    private val downloadDir: Path,
    private val userAgents: List<String>
) : AutoCloseable {
    lateinit var page: Page
    private lateinit var browser: BrowserContext
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private var currentUserAgent = 0

    init {
        setup()
    }

    fun switchUserAgent() {
        page.close()
        browser.close()
        setup()
    }

    private fun setup() {
        browser = playwright.firefox().launchPersistentContext(
            profilePath,
            options.setUserAgent(userAgents[currentUserAgent % userAgents.size])
        )
        logger.info("Switched to new BrowserContext with UA: ${userAgents[currentUserAgent % userAgents.size]}")
        page = browser.newPage()
        currentUserAgent++

        // Setup download events
        page.onDownload { download: Download ->
            logger.info("Download event from ${page.url()}")
            download.saveAs(downloadDir.resolve("file-${UUID.randomUUID()}"))
        }
    }

    override fun close() {
        page.close()
        browser.close()
    }

}