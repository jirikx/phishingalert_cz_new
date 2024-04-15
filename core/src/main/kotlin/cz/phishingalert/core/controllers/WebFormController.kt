package cz.phishingalert.core.controllers

import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.Authors
import cz.phishingalert.common.repository.*
import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.common.domain.PhishingAccidents
import cz.phishingalert.core.RepositoryService
import jakarta.servlet.http.HttpServletRequest
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import java.net.URL
import java.time.LocalDateTime


@Controller
class WebFormController(
    val repositoryService: RepositoryService
) {
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
        val ip = request.remoteAddr
        val author = Author(0, form.name_author, form.email_author, userAgent, ip ?: "unknown")
        val accident = PhishingAccident(0, URL(form.website), LocalDateTime.now(), false, form.note_text, form.email, form.phone)

        println(author)
        println(accident)
        repositoryService.save(author, accident)

        return "success"
    }

    @GetMapping(path = ["/nevim"])
    fun getIp() = ResponseEntity.ok("everything's alright")
}