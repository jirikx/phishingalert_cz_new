package cz.phishingalert.core

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean



@SpringBootApplication(scanBasePackages = ["cz.phishingalert"])
class CoreApplication {
//	@Bean
//	fun myQueue(): Queue {
//		return Queue("myQueue", false)
//	}

//	@Bean
//	fun runner(template: RabbitTemplate): ApplicationRunner {
//		return ApplicationRunner {
//			template.convertAndSend("myQueue", "Hello there :)")
//		}
//	}
}

fun main(args: Array<String>) {
	runApplication<CoreApplication>(*args)
}