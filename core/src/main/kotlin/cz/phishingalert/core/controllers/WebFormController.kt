package cz.phishingalert.core.controllers

import cz.phishingalert.common.messagequeue.ScrapingMessage
import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.core.communication.EmailSender
import cz.phishingalert.core.communication.MessageQueueSender
import cz.phishingalert.core.services.RepositoryService
import cz.phishingalert.core.configuration.CoreConfig
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Controller
class WebFormController(
    val messageQueueSender: MessageQueueSender,
    val emailSender: EmailSender,
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
    ): ModelAndView {
        repositoryService.createTablesIfNotExist()
        val uuid = UUID.randomUUID()    // This UUID is used as a token for confirmation email

        val author = Author(0, form.name_author, form.email_author, userAgent, request.remoteAddr ?: "unknown")
        val accident = PhishingAccident(
            0,
            URL(form.website),
            LocalDateTime.now(),
            false,
            form.note_text,
            form.email,
            form.phone,
            guid=uuid
        )
        accident.id = repositoryService.save(author, accident)

        logger.info(author.toString())
        logger.info(accident.toString())

        // Send email to the report's author if the confirmation is required by the application config
        if (config.requireEmailConfirmation) {
            emailSender.sendEmail(
                "jirikonvicny@email.cz",
                author.email,
                "[phishingalert.cz] Potvrďte Váš report",
                """
                Děkujeme za hlášení. 
                Pro potvrzení klikněte na následující odkaz: http://localhost:8080/confirm/$uuid 
                
                Phishingalert.cz tým"""
            )

            return ModelAndView("success")
        }

        sendMessageToScraper(accident)
        return ModelAndView("success")
    }

    @GetMapping(path = ["/confirm/{rawGuid}"])
    fun confirmAccident(@PathVariable rawGuid: String): ModelAndView {
        try {
            val accident = repositoryService.readAccidentByGuid(UUID.fromString(rawGuid))

            // Handle non-existent accident
            if (accident == null) {
                logger.warn("Accident with guid=$rawGuid wasn't found.")
                return ModelAndView("error/404", HttpStatus.NOT_FOUND)
            }

            // Handle accident which was already confirmed in the past
            if (accident.confirmed) {
                val response = ModelAndView("error/errorPage", HttpStatus.METHOD_NOT_ALLOWED)
                response.addObject(
                    "message",
                    "Phishing accident was already confirmed before."
                )
                return response
            }

            // Change accident status to confirmed and notify the Scraper
            repositoryService.confirmAccident(accident)
            logger.info("Accident with guid=$rawGuid was confirmed!")
            sendMessageToScraper(accident)
        } catch (ex: IllegalArgumentException) {
            logger.warn("There was a problem with guid=$rawGuid.")
            return ModelAndView("error/404", HttpStatus.NOT_FOUND)
        }

        return ModelAndView("success")
    }

    /**
     * Sends the message with scraping request to the Scraper
     */
    private fun sendMessageToScraper(accident: PhishingAccident) {
        var shouldCrawl = true

        // Check if the time limit between similar accident reporting already passed
        val timeDiff = Duration.between(repositoryService.timeOfLastSimilarAccident(accident), LocalDateTime.now())
        if (timeDiff.toMinutes() < config.minTimeDiff)
            shouldCrawl = false

        if (accident.id != null) {
            val message = ScrapingMessage(
                accident.id!!,
                accident.url.toString(),
                LocalDateTime.now().toString(),
                shouldCrawl,
                config.sftpDirectory
            )

            messageQueueSender.send(message)
        } else {
            logger.error("Cannot send message because of missing accidentId!")
        }
    }

}