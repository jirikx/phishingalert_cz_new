package cz.phishingalert.common

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class SharedClass {
    @Autowired
    private lateinit var env: Environment

    fun sayHello() {
        println("Hello from the shared class, ${env.getProperty("surname")}, \n ${System.getProperty("java.class.path")}")
    }
}