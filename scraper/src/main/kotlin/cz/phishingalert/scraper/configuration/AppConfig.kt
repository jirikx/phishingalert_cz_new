package cz.phishingalert.scraper.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "settings")
data class AppConfig(
    val timeLimit: Int,
    val downloaderConfig: DownloaderConfig
) {
    data class DownloaderConfig(
        val filePath: String,
        val firefoxProfilePath: String
    )
}