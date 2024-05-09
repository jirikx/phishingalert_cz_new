package cz.phishingalert.core.controllers

import cz.phishingalert.common.messagequeue.ScrapingMessage
import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.core.MessageQueueSender
import cz.phishingalert.core.RepositoryService
import cz.phishingalert.core.configuration.CoreConfig
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime

@Controller
class WebFormController(
    val messageQueueSender: MessageQueueSender,
    val repositoryService: RepositoryService,
    val config: CoreConfig
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Class for transferring data from of the web form
     */
    data class WebForm(
        val name_author: String,
        val email_author: String,
        val email: String?,
        val phone: String?,
        val website: String,
        val sms: String?,
        val email_header: String?,
        val email_text: String?,
        val note_text: String?
    )

    @PostMapping(path = ["/submitForm"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun create(
        @RequestHeader(value = HttpHeaders.USER_AGENT) userAgent: String,
        @ModelAttribute form: WebForm,
        request: HttpServletRequest
    ): String {
        var shouldCrawl = true
        val ip = request.remoteAddr
        val author = Author(0, form.name_author, form.email_author, userAgent, ip ?: "unknown")
        val accident = PhishingAccident(
            0,
            URL(form.website),
            LocalDateTime.now(),
            false,
            form.note_text,
            form.email,
            form.phone
        )

        logger.info(author.toString())
        logger.info(accident.toString())

        // Check if the time limit between similar accident reporting already passed
        val timeDiff = Duration.between(repositoryService.timeOfLastSimilarAccident(accident), LocalDateTime.now())
        if (timeDiff.toMinutes() < config.minTimeDiff)
            shouldCrawl = false

        val accidentId = repositoryService.save(author, accident)
        if (accidentId != null) {
            val message = ScrapingMessage(
                accidentId,
                form.website,
                LocalDateTime.now().toString(),
                shouldCrawl,
                config.sftpDirectory
            )

            messageQueueSender.send(message)
        } else {
            logger.error("Cannot send message because of missing accidentId")
        }

        return "success"
    }

}