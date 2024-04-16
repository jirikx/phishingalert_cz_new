package cz.phishingalert.scraper

import cz.phishingalert.scraper.configuration.AppConfig
import cz.phishingalert.scraper.input.MessageQueueProcessor
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.xbill.DNS.Message

@SpringBootApplication(scanBasePackages = ["cz.phishingalert"])
@EnableConfigurationProperties(AppConfig::class, AppConfig.CrawlerConfig::class)
class ScraperApplication(
    val orchestrator: Orchestrator
) : ApplicationRunner {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        if (args.containsOption("try-domain") && args.getOptionValues("try-domain").isNotEmpty()) {
            logger.info("Started scraper in demo mode. URL: ${args.getOptionValues("try-domain").first()}")
            orchestrator.scrape(args.getOptionValues("try-domain").first())
        } else {
            logger.info("Started scraper in MessageQueue mode. Listening for messages.")
        }
    }

}

fun main(args: Array<String>) {
    val app = SpringApplication(ScraperApplication::class.java)
    if (args.any { it.startsWith("--try-domain") })
        app.setAdditionalProfiles("command-line")
    else
        app.setAdditionalProfiles("message-queue")
    app.run(*args)
}
