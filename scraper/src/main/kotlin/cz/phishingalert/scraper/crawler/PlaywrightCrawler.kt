package cz.phishingalert.scraper.crawler

import com.microsoft.playwright.*
import cz.phishingalert.scraper.createSubDirectory
import org.springframework.stereotype.Component
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayDeque


@Component
class PlaywrightCrawler(
    val playwright: Playwright,
    val options: BrowserType.LaunchPersistentContextOptions
) : Crawler() {
    private val profilePath = Paths.get("/home/jirik/snap/firefox/common/.mozilla/firefox/53qm6ryg.TestProfile")
    val visitedLinksLimit = 10

    override fun crawl(url: URL, downloadDir: Path) {
        val browser = playwright.firefox().launchPersistentContext(profilePath, options)

        // Take the screenshot of the main page
        val page = browser.newPage()
        page.navigate(url.toString())
        page.screenshot(Page.ScreenshotOptions()
            .setPath(downloadDir.resolve("screenshot-${UUID.randomUUID()}.png"))
            .setFullPage(true))
        logger.info("Took a screenshot of ${page.url()}")

        val queue = ArrayDeque<URI>()
        val alreadyFoundLinks = HashSet<URI>()

        queue.add(url.toURI())
        alreadyFoundLinks.add(url.toURI())

        // Setup download events
        page.onDownload { download: Download ->
            logger.info("Download event from ${page.url()}")
            download.saveAs(downloadDir.resolve("file-${UUID.randomUUID()}"))
        }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            logger.info("Page: $current")

            try {
                page.navigate(current.toString())
            } catch (ex: PlaywrightException) {
                logger.warn("Problem navigating to $current, ${ex.message}")
                continue
            }

            // Download current webpage
            downloadHtml(page, downloadDir)
            downloadJavaScript(page, downloadDir)
            downloadImages(page, downloadDir)
            downloadCss(page, downloadDir)

            // Stop adding new links if the limit was reached
            if (alreadyFoundLinks.size >= visitedLinksLimit)
                continue

            // Process the links referenced by the current page
            val links = discoverLinks(page, url, true)
            for (link in links) {
                if (alreadyFoundLinks.size < visitedLinksLimit && !alreadyFoundLinks.contains(link)) {
                    queue.add(link)
                    alreadyFoundLinks.add(link)
                }
            }
        }

        logger.info("Crawling of $url has finished")
    }

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

    fun downloadJavaScript(page: Page, downloadDir: Path) {
        // Extract all links to JS from the page (without duplicates)
        val jsLinks = page.querySelectorAll("script").mapNotNull { it.getAttribute("src") }.toSet()


        downloadFromUris(fixUris(page.url(), jsLinks), createSubDirectory(downloadDir, "js"))
    }

    fun downloadImages(page: Page, downloadDir: Path) {
        // Extract all image links from the page (without duplicates)
        val imageLinks = page.querySelectorAll("img").mapNotNull { it.getAttribute("src") }.toSet()

        downloadFromUris(fixUris(page.url(), imageLinks), createSubDirectory(downloadDir, "img"))
    }

    fun downloadCss(page: Page, downloadDir: Path) {
        // Extract all links to CSS from the page (without duplicates)
        val cssLinks = page.querySelectorAll("link[rel=\"stylesheet\"]").mapNotNull { it.getAttribute("href") }.toSet()

        downloadFromUris(fixUris(page.url(), cssLinks), createSubDirectory(downloadDir, "css"))
    }

}