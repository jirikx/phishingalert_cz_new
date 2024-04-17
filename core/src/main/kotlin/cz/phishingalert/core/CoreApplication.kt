package cz.phishingalert.core

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.thymeleaf.expression.Dates
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SpringBootApplication(scanBasePackages = ["cz.phishingalert"])
class CoreApplication {

}

fun main(args: Array<String>) {
	runApplication<CoreApplication>(*args)
}