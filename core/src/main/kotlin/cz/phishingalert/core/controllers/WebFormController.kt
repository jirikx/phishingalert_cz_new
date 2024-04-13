package cz.phishingalert.core.controllers

import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.Authors
import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.common.domain.PhishingAccidents
import cz.phishingalert.common.repository.AuthorRepository
import cz.phishingalert.common.repository.PhishingAccidentRepository
import cz.phishingalert.common.repository.WebsiteRepository
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import java.time.LocalDateTime


@Controller
@Transactional
class WebFormController(
    val authorRepository: AuthorRepository,
    val phishingAccidentRepository: PhishingAccidentRepository
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
        request: ServerHttpRequest
    ): ResponseEntity<String> {
        val ip = request.remoteAddress?.address?.hostAddress
        val author = Author(0, form.name_author, form.email_author, userAgent, ip ?: "unknown")
        val accident = PhishingAccident(0, LocalDateTime.now(), false, form.note_text, form.email, form.phone)

        println(author)
        println(accident)
        save(author, accident)

        return ResponseEntity<String>(HttpStatus.OK)
    }

    fun save(author: Author, accident: PhishingAccident) {
        if (!Authors.exists())
            SchemaUtils.create(Authors)
        if (!PhishingAccidents.exists())
            SchemaUtils.create(PhishingAccidents)

        val inserted = authorRepository.create(author)
        accident.authorId = inserted.id!!
        phishingAccidentRepository.create(accident)
    }

    @GetMapping(path = ["/nevim"])
    fun getIp(request: ServerHttpRequest) = ResponseEntity.ok("everything's alright")

}