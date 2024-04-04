package cz.phishingalert.scraper.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.nio.file.Path

@ConfigurationProperties(prefix = "settings")
data class AppConfig(
    val timeLimit: Int,
    val defaultWhoIsServer: String,
    val defaultRDAPServer: String,
    val crawlerConfig: CrawlerConfig
) {
    @ConfigurationProperties(prefix = "settings.crawler-config")
    data class CrawlerConfig(
        val browserProfilePath: Path,
        val visitedPagesLimit: Int,
        val triesPerPageLimit: Int,
        val userAgents: List<String>
    )
}