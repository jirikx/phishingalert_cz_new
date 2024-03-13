package cz.phishingalert.scraper

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.net.InetAddress


@SpringBootApplication
class ScraperApplication : ApplicationRunner {
    @RabbitListener(queues = ["myQueue"])
    fun listen(msg: String) {
        println("Message read from myQueue : $msg")

    }

    override fun run(args: ApplicationArguments) {
        if (args.containsOption("try-domain") && args.getOptionValues("try-domain").isNotEmpty())
            println(args.getOptionValues("try-domain").first())
    }

}

fun main(args: Array<String>) {
    runApplication<ScraperApplication>(*args)

    val domain = "www.amazon.com"
    val address = InetAddress.getByName(domain)
    println("IP address of $domain is ${address.hostAddress}")
}
