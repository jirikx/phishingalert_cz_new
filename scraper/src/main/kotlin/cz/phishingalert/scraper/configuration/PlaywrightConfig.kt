package cz.phishingalert.scraper.configuration

import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
@Configuration
class PlaywrightConfig {
    @Bean
    fun playwright(): Playwright {
        return Playwright.create()
    }

    @Bean
    fun options(): BrowserType.LaunchPersistentContextOptions {
        return BrowserType.LaunchPersistentContextOptions().setSlowMo(50.0).setAcceptDownloads(true).setHeadless(true)
    }
}

