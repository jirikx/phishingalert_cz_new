package cz.phishingalert.scraper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ScraperApplication

fun main(args: Array<String>) {
    runApplication<ScraperApplication>(*args)
}
