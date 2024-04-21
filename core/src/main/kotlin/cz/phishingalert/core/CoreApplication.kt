package cz.phishingalert.core

import cz.phishingalert.core.configuration.ReportAuthor
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["cz.phishingalert"])
@EnableConfigurationProperties(ReportAuthor::class)
class CoreApplication

fun main(args: Array<String>) {
	runApplication<CoreApplication>(*args)
}