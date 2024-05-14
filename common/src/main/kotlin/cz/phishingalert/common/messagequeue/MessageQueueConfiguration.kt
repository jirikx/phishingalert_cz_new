package cz.phishingalert.common.messagequeue

import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for AMQP communication between different parts of the application (scraper and core)
 */
@Configuration
class MessageQueueConfiguration {
    /**
     * Create the message queue
     * It doesn't matter if this bean is created multiple times because the queue declaration is an idempotent operation
     */
    @Bean
    fun queue(): Queue {
        return Queue(QUEUE_NAME, false)
    }

    /**
     * Handle serialization and deserialization of the objects in the message queue
     */
    @Bean
    fun jsonMessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }
}