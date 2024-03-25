package cz.phishingalert.scraper.crawler

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import cz.phishingalert.scraper.createSubDirectory
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.HashSet

@Component
class PlaywrightCrawler(
    val playwright: Playwright,
    val options: BrowserType.LaunchPersistentContextOptions
) : Crawler() {
    private val profilePath = Paths.get("/home/jirik/snap/firefox/common/.mozilla/firefox/53qm6ryg.TestProfile")
    val visitedLinksLimit = 100

    override fun crawl(url: URL, downloadDir: Path) {
        val browser = playwright.firefox().launchPersistentContext(profilePath, options)

        val page = browser.newPage()
        page.navigate(url.toString())
        page.screenshot(Page.ScreenshotOptions()
            .setPath(downloadDir.resolve("screenshot-${UUID.randomUUID()}.png"))
            .setFullPage(true))
        println("completed, ${page.url()}")

        downloadImages(page, downloadDir)
        downloadCss(page, downloadDir)
        return

        val pageContent = page.content()
        pageContent.let {
            content -> File(downloadDir.resolve("index.html").toString()).writeText(content)
        }

        val queue = ArrayDeque<String>()
        val visited = HashSet<String>()
        var foundLinks = 0

        queue.add(url.toString())
        visited.add(url.toString())

        while (queue.isNotEmpty() && foundLinks < visitedLinksLimit) {
            val current = queue.removeFirst()

            // Download current webpage
            processLink(current)

            val currentlyVisited = HashSet<String>()

            // Process the links referenced by the current page
            val links = page.querySelectorAll("[href]")
            for (link in links) {
                val attribute = link.getAttribute("href")
                if (!visited.contains(attribute) && !currentlyVisited.contains(attribute)) {
                    currentlyVisited.add(attribute)
                    queue.add(attribute)
                    foundLinks++
                }
            }

            visited.add(current)
        }

        println("end")
    }

    fun downloadImages(page: Page, downloadDir: Path) {
        // Extract all image links from the page (without duplicates)
        val imageLinks = page.querySelectorAll("img").map { it.getAttribute("src") }.toSet()

        downloadFromUris(fixUris(page.url(), imageLinks), createSubDirectory(downloadDir, "img"))
    }

    fun downloadCss(page: Page, downloadDir: Path) {
        // Extract all links to CSS from the page (without duplicates)
        val cssLinks = page.querySelectorAll("link[rel=\"stylesheet\"]").map { it.getAttribute("href") }.toSet()

        downloadFromUris(fixUris(page.url(), cssLinks), createSubDirectory(downloadDir, "css"))
    }

    fun processLink(link: String) {
        println(link)
    }
}