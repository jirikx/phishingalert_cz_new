package cz.phishingalert.scraper.crawler

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.AriaRole
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class PlaywrightCrawler : Crawler {
    private val extensionPath = "./IStillDontCareAboutCookies"
    private val playwright: Playwright = Playwright.create()
    private val options: BrowserType.LaunchPersistentContextOptions = BrowserType.LaunchPersistentContextOptions()
        .setArgs(listOf(
            "--headless=false",
            "--disable-extensions-except=$extensionPath",
            "--load-extension=$extensionPath"))
        .setSlowMo(50.0)

    override fun crawl(url: URL, downloadDir: Path) {
        val browser = playwright.chromium().launchPersistentContext(downloadDir, options)

        val page = browser.newPage()
        page.navigate(url.toString())
        page.screenshot(Page.ScreenshotOptions().setPath(Paths.get("screenshot-${UUID.randomUUID()}.png")).setFullPage(true))
        println("completed")

        for (link in page.getByRole(AriaRole.LINK).all())
            println(link.getAttribute("href"))

//        val links = page.querySelectorAll("[href]")
//        for (link in links)
//            println(link.getAttribute("href"))



        //for (button in page.getByRole(AriaRole.BUTTON))
    }
}