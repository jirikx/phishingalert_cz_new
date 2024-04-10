package cz.phishingalert.scraper

import cz.phishingalert.scraper.configuration.AppConfig
import cz.phishingalert.scraper.repository.WebsiteRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import something.SharedClass


@SpringBootApplication
@EnableConfigurationProperties(AppConfig::class, AppConfig.CrawlerConfig::class)
class ScraperApplication(val orchestrator: Orchestrator) : ApplicationRunner {
    private val logger = LoggerFactory.getLogger(javaClass)

    //@RabbitListener(queues = ["myQueue"])
    fun listen(msg: String) {
        logger.info("Message read from myQueue : $msg")

    }

    override fun run(args: ApplicationArguments) {
        if (args.containsOption("try-domain") && args.getOptionValues("try-domain").isNotEmpty())
            logger.info("URL: ${args.getOptionValues("try-domain").first()}")

//        transaction {
//            val id = Websites.insert {
//                it[this.url] = "random.cz"
//                it[this.domainHolder] = "Some random domain holder a.s."
//                it[this.registrationDate] = LocalDateTime.now()
//            }
//
//            Websites.insert {
//                it[this.url] = "fgegreg.com"
//                it[this.domainHolder] = "AAAAA a.s."
//                it[this.registrationDate] = LocalDateTime.now()
//            }
//        }
//
//        transaction {
//            val query = Websites.selectAll()
//            query.forEach {
//                println(it)
//            }
//        }

        orchestrator.scrape(args.getOptionValues("try-domain").first())
        SharedClass().sayHello()
    }

}

fun main(args: Array<String>) {
    runApplication<ScraperApplication>(*args)
}
