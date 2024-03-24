package cz.phishingalert.scraper.crawler

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import org.springframework.stereotype.Component
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.HashSet

@Component
class PlaywrightCrawler(
    val playwright: Playwright,
    val options: BrowserType.LaunchPersistentContextOptions
) : Crawler {
    private val profilePath = Paths.get("/home/jirik/snap/firefox/common/.mozilla/firefox/53qm6ryg.TestProfile")
    val visitedLinksLimit = 100

    override fun crawl(url: URL, downloadDir: Path) {
        val browser = playwright.firefox().launchPersistentContext(profilePath, options)

        val page = browser.newPage()
        page.navigate(url.toString())
        page.screenshot(Page.ScreenshotOptions()
            .setPath(downloadDir.resolve("screenshot-${UUID.randomUUID()}.png"))
            .setFullPage(true))
        println("completed")

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

    fun processLink(link: String) {
        println(link)
    }
}