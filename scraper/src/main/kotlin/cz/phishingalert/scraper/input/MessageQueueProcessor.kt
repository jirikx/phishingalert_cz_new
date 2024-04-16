package cz.phishingalert.scraper.input

import cz.phishingalert.common.messagequeue.QUEUE_NAME
import cz.phishingalert.common.messagequeue.ScrapingMessage
import cz.phishingalert.scraper.Orchestrator
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("message-queue")
class MessageQueueProcessor(val orchestrator: Orchestrator) : InputProcessor {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [QUEUE_NAME])
    fun listen(message: ScrapingMessage) {
        logger.info("Message read from $QUEUE_NAME : $message")
        orchestrator.scrape(message.url)
    }
}