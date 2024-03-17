package cz.phishingalert.scraper

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "settings")
data class AppConfig(
    val timeLimit: Int,
    val downloaderConfig: DownloaderConfig
) {
    data class DownloaderConfig(
        val filePath: String,
    )
}