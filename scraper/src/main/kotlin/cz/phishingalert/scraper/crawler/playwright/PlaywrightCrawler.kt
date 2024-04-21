package cz.phishingalert.scraper.crawler.playwright

import com.microsoft.playwright.*
import cz.phishingalert.scraper.configuration.AppConfig
import cz.phishingalert.scraper.crawler.Crawler
import cz.phishingalert.scraper.utils.createSubDirectory
import org.springframework.http.HttpStatus
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayDeque

class PlaywrightCrawler(
    val playwright: Playwright,
    val options: BrowserType.LaunchPersistentContextOptions,
    val config: AppConfig.CrawlerConfig
) : Crawler() {
    override fun crawl(url: URL, downloadDir: Path) {
        val process = CrawlingProcess(playwright, options, config.browserProfilePath, downloadDir, config.userAgents)

        process.use {
            // Take a screenshot of the main page
            try {
                tryToNavigate(process, url)
                process.page.screenshot(Page.ScreenshotOptions()
                    .setPath(downloadDir.resolve("screenshot-${LocalDateTime.now()}.png"))
                    .setFullPage(true))
                logger.info("Took a screenshot of ${process.page.url()}")
            } catch (ex: PlaywrightException) {
                logger.warn("Problem navigating to $url, ${ex.message}")
            }

            val queue = ArrayDeque<URI>()
            val alreadyFoundLinks = HashSet<URI>()

            queue.add(url.toURI())
            alreadyFoundLinks.add(url.toURI())

            // Browse the links with BFS algorithm
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                logger.info("Page: $current")

                try {
                    tryToNavigate(process, url)
                } catch (ex: PlaywrightException) {
                    logger.warn("Problem navigating to $current, ${ex.message}")
                    continue
                }

                // Download current webpage
                downloadHtml(process.page, downloadDir)
                downloadJavaScript(process.page, url, downloadDir)
                downloadImages(process.page, url, downloadDir)
                downloadCss(process.page, url, downloadDir)

                // Stop adding new links if the limit was reached
                if (alreadyFoundLinks.size >= config.visitedPagesLimit)
                    continue

                // Process the links referenced by the current page
                val links = discoverLinks(process.page, url, config.allowOutsideDomain)
                for (link in links) {
                    if (alreadyFoundLinks.size < config.visitedPagesLimit && !alreadyFoundLinks.contains(link)) {
                        queue.add(link)
                        alreadyFoundLinks.add(link)
                    }
                }
            }

            logger.info("Crawling of $url has finished")
        }

    }

    /**
     * Try to navigate the website with given url and react to the website's response
     */
    fun tryToNavigate(process: CrawlingProcess, url: URL) {
        var triesCount = 1
        var status = process.page.navigate("$url").status()
        while (triesCount <= config.triesPerPageLimit && status == HttpStatus.TOO_MANY_REQUESTS.value()) {
            process.switchUserAgent()
            status = process.page.navigate("$url").status()
            triesCount++
        }

        // Handle the scenario where we ran out of tries and the website still didn't let us in
        if (status == HttpStatus.TOO_MANY_REQUESTS.value()) {
            throw PlaywrightException("Couldn't navigate to $url after $triesCount tries, HTTP status: $status")
        }
    }

    /**
     * Discover weblinks which are available from the current page
     */
    fun discoverLinks(page: Page, url: URL, allowOutsideDomain: Boolean): List<URI> {
        val links = page.querySelectorAll("[href]")
            .map { it.getAttribute("href") }
            .mapNotNull { fixUri(page.url(), it) }
            .filter {
                val link = it.path
                //logger.info("$it has host ${it.host}")
                !link.endsWith(".css") && !link.endsWith(".js") && !link.endsWith(".png") &&
                !link.endsWith(".jpg") && !link.endsWith(".jpeg") && !link.endsWith(".svg") &&
                !link.endsWith(".gif")
            }

        return if (allowOutsideDomain)
            links
        else
            links.filter { it.host == url.host }    //fixme: add subdomain filtering
    }

    fun downloadHtml(page: Page, downloadDir: Path) {
        val pageContent = page.content()
        pageContent.let {
                content -> File(downloadDir.resolve("index-${UUID.randomUUID()}.html").toString()).writeText(content)
        }
    }

    fun downloadJavaScript(page: Page, url: URL, downloadDir: Path) {
        // Extract all links to JS from the page (without duplicates)
        val jsLinks = page.querySelectorAll("script").mapNotNull { it.getAttribute("src") }.toSet()

        downloadFromUris(fixUris(url.toString(), jsLinks), createSubDirectory(downloadDir, "js"))
    }

    fun downloadImages(page: Page, url: URL, downloadDir: Path) {
        // Extract all image links from the page (without duplicates)
        val imageLinks = page.querySelectorAll("img").mapNotNull { it.getAttribute("src") }.toSet()

        downloadFromUris(fixUris(url.toString(), imageLinks), createSubDirectory(downloadDir, "img"))
    }

    fun downloadCss(page: Page, url: URL, downloadDir: Path) {
        // Extract all links to CSS from the page (without duplicates)
        val cssLinks = page.querySelectorAll("link[rel=\"stylesheet\"]").mapNotNull { it.getAttribute("href") }.toSet()

        downloadFromUris(fixUris(url.toString(), cssLinks), createSubDirectory(downloadDir, "css"))
    }

}