package cz.phishingalert.scraper.downloaders

import com.microsoft.playwright.Playwright
import cz.phishingalert.scraper.configuration.AppConfig
import cz.phishingalert.common.domain.Website
import cz.phishingalert.scraper.downloaders.parsers.WebsiteInfoParser
import cz.phishingalert.scraper.utils.checkURL
import cz.phishingalert.scraper.utils.toRootDomain
import org.apache.commons.net.whois.WhoisClient
import org.springframework.stereotype.Component
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import org.springframework.http.HttpStatus
import java.io.IOException
import java.net.SocketException
import kotlin.math.log


private const val WHOIS_PORT = 43
private const val MAX_REDIRECT_COUNT = 3

@Component
class WebsiteDownloader(
    val appConfig: AppConfig
) : Downloader<Website>() {

    override fun download(url: URL): List<Website> {
        // Try a RDAP request first, and if it fails, fallback to WhoIs
        val rootDomain = toRootDomain(url.host)
        var result = makeRDAPRequest(rootDomain)
        if (result == null) {
            result = makeWhoIsRequest(rootDomain) ?: return emptyList()
        }

        return listOf(result)
    }

    fun makeWhoIsRequest(domain: String): Website? {
        var currentServer: String? = appConfig.defaultWhoIsServer
        var previousServer: String? = appConfig.defaultWhoIsServer
        val ianaRegex = Regex("refer:\\s+(.*)", RegexOption.MULTILINE)  // match the corresponding server for the TLD
        val commonRegex = Regex("registrar WHOIS server: (.*?)\\s", RegexOption.IGNORE_CASE) // match pointers to next servers

        val whoIsClient = WhoisClient()
        whoIsClient.connect(currentServer, WHOIS_PORT)

        // Query the IANA to get the server for given TLD
        var results = whoIsClient.query(domain).trimIndent()
        currentServer = ianaRegex.find(results)?.groupValues?.get(1)
        if (currentServer == null || !checkURL(currentServer)) {
            whoIsClient.disconnect()
            logger.error("Problem with querying $currentServer for the WhoIs record of $domain")
            return null
        }

        // Make requests to referenced servers until we get to the final one
        try {
            var redirectCount = 0
            while (currentServer != previousServer && redirectCount++ < MAX_REDIRECT_COUNT) {
                previousServer = currentServer
                whoIsClient.connect(currentServer, WHOIS_PORT)
                results = whoIsClient.query(domain).trimIndent()
                currentServer = commonRegex.find(results)?.groupValues?.get(1)

                if (currentServer == null || !checkURL(currentServer))
                    break
            }

            return WebsiteInfoParser.parseWhoIs(results)
        } catch (ex: SocketException) {
            logger.error("Problem with connection to the WhoIs server: ${ex.message}")
            return null
        } catch (ex: IOException) {
            logger.error("Problem with IO during WhoIs request: ${ex.message}")
            return null
        } finally {
            whoIsClient.disconnect()
        }

    }

    /**
     * Make RDAP request with a HttpClient
     */
    fun makeRDAPRequest(domain: String): Website? {
        logger.warn("DOMAIN RDAP: $domain")
        val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS).build()
        val request = HttpRequest.newBuilder()
            .uri(URI("${appConfig.defaultRDAPServer}${domain}"))
            .timeout(Duration.ofSeconds(10)).GET().build()

        val response = client.send(request, BodyHandlers.ofString())

        // Handle server response, for more info: https://about.rdap.org/
        if (response.statusCode() != HttpStatus.OK.value()) {
            logger.error("RDAP request to ${appConfig.defaultRDAPServer} failed with status code ${response.statusCode()}")
            return null
        }

        logger.info("RDAP request for $domain got response code ${response.statusCode()}")
        return WebsiteInfoParser.parseRDAP(response.body())
    }
}