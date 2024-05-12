package cz.phishingalert.core.communication

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service

@Service
class EmailSender(private val javaMailSender: JavaMailSender = JavaMailSenderImpl()) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Send email [from] [to] with [subject] and [text]
     */
    fun sendEmail(
        from: String,
        to: String,
        subject: String,
        text: String
    ) {
        val message = SimpleMailMessage()
        message.from = from
        message.setTo(to)
        message.subject = subject
        message.text = text

        try {
            javaMailSender.send(message)
            logger.info("Mail from $from to $to was sent.")
        } catch (ex: MailException) {
            logger.error("MailException when sending mail from $from to $to, exception message: ${ex.message}")
        }
    }
}