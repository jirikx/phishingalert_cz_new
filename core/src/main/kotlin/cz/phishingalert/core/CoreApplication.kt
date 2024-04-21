package cz.phishingalert.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["cz.phishingalert"])
class CoreApplication {

}

fun main(args: Array<String>) {
	runApplication<CoreApplication>(*args)
}