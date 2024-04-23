package cz.phishingalert.core

import cz.phishingalert.core.configuration.CoreConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["cz.phishingalert"])
@EnableConfigurationProperties(CoreConfig::class, CoreConfig.ReportAuthor::class)
class CoreApplication

fun main(args: Array<String>) {
	runApplication<CoreApplication>(*args)
}