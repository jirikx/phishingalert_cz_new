package cz.phishingalert.scraper.downloaders

import com.microsoft.playwright.BrowserType.LaunchOptions
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import cz.phishingalert.scraper.configuration.AppConfig
import cz.phishingalert.scraper.checkURL
import org.apache.commons.net.whois.WhoisClient
import org.springframework.stereotype.Component
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Paths
import java.time.Duration


private const val WHOIS_PORT = 43
private const val MAX_REDIRECT_COUNT = 3
private const val LOCAL_DOWNLOAD_PATH = "/tmp/crawled-content/" //todo: move to config file!

@Component
class WebsiteDownloader(
    val appConfig: AppConfig,
    val playwright: Playwright
) : Downloader {

    override fun download(url: URL) {
        // find a location where to store the page
        // https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.io.path/create-temp-file.html
        println("Storing to: ${appConfig.downloaderConfig.filePath}")
        val browser = playwright.chromium().launch()

        val page = browser.newPage()
        page.navigate("https://arh.antoinevastel.com/bots/areyouheadless/")
        page.screenshot(Page.ScreenshotOptions().setPath(Paths.get("cf7.png")).setFullPage(true))
        playwright.firefox().launch(LaunchOptions().setHeadless(false).setSlowMo(50.0))
    }

    fun makeWhoIsRequest(domain: String): Boolean {
        var currentServer: String? = "whois.iana.org"
        var previousServer: String? = "whois.iana.org"
        val ianaRegex = Regex("refer:\\s+(.*)", RegexOption.MULTILINE)  // match the corresponding server for the TLD
        val commonRegex = Regex("registrar WHOIS server: (.*?)\\s", RegexOption.IGNORE_CASE) // match pointers to next servers

        val whoIsClient = WhoisClient()
        whoIsClient.connect(currentServer, WHOIS_PORT)

        // Query the IANA to get the server for given TLD
        var results = whoIsClient.query(domain).trimIndent()
        currentServer = ianaRegex.find(results)?.groupValues?.get(1)
        if (currentServer == null || !checkURL(currentServer)) {
            whoIsClient.disconnect()
            return false
        }

        // Make requests to referenced servers until we get to the final one
        var redirectCount = 0
        while (currentServer != previousServer && redirectCount++ < MAX_REDIRECT_COUNT) {
            previousServer = currentServer
            whoIsClient.connect(currentServer, WHOIS_PORT)
            results = whoIsClient.query(domain).trimIndent()
            currentServer = commonRegex.find(results)?.groupValues?.get(1)

            if (currentServer == null || !checkURL(currentServer))
                break
        }

        whoIsClient.disconnect()
        println(results)
        return true
    }

    fun makeRDAPRequest(domain: String): Boolean {
        val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS).build()
        val request = HttpRequest.newBuilder()
            .uri(URI("https://rdap.org/domain/${domain}"))
            .timeout(Duration.ofSeconds(10)).GET().build()

        val response = client.send(request, BodyHandlers.ofString())
        println(response.body())
        return true
    }
}