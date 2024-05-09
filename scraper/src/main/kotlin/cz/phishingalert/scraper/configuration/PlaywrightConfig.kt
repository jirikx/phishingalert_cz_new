package cz.phishingalert.scraper.configuration

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class PlaywrightConfig {
    fun options(): BrowserType.LaunchPersistentContextOptions {
        return BrowserType.LaunchPersistentContextOptions()
            .setSlowMo(1.0)
            .setAcceptDownloads(true)
            .setHeadless(true)
            .setLocale("cs-CZ")
            .setTimezoneId("Europe/Prague")
            .setViewportSize(1920, 1080)
    }
}