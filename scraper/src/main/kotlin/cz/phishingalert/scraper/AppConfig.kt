package cz.phishingalert.scraper

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "settings")
data class AppConfig(
    val timeLimit: Int
)