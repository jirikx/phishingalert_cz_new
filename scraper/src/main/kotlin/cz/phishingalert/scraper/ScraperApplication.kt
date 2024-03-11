package cz.phishingalert.scraper

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class ScraperApplication {
    @RabbitListener(queues = ["myQueue"])
    fun listen(msg: String) {
        println("Message read from myQueue : $msg")
    }
}

fun main(args: Array<String>) {
    runApplication<ScraperApplication>(*args)
}
