package cz.phishingalert.core

import cz.phishingalert.common.messagequeue.QUEUE_NAME
import cz.phishingalert.common.messagequeue.ScrapingMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.AmqpException
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

/**
 * Handle sending of messages through RabbitMQ
 */
@Service
class MessageQueueSender(val rabbitTemplate: RabbitTemplate) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Send given [message] to the queue identified by [QUEUE_NAME]
     */
    fun send(message: ScrapingMessage) {
        logger.info("Sending $message via $QUEUE_NAME")

        try {
            rabbitTemplate.convertAndSend(QUEUE_NAME, message)
        } catch (ex: AmqpException) {
            logger.error("Cannot send $message via $QUEUE_NAME, exception: ${ex.message}")
        }
    }

}