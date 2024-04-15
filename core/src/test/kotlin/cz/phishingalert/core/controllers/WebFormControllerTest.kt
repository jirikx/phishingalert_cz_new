package cz.phishingalert.core.controllers

import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.core.RepositoryService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(WebFormController::class)
class WebFormControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @MockBean lateinit var repositoryService: RepositoryService

    @Test
    fun testThatControllerReturnsHtmlPage() {
        val author = Author(1, "Bob", "mail@gmail.com", "UA", "ipAddr")
        val accident = PhishingAccident(
            1,
            LocalDateTime.MIN,
            false,
            "note",
            null,
            "+420",
            1)

        val request = post("/submitForm")
            .accept(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .header("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:124.0) Gecko/20100101 Firefox/124.0")
            .content("name_author=ads&email_author=dkl%40email.com&email=dvd%40dc.com&phone=&website=https%3A%2F%2Fdds.com&sms=&email_header=f&email_text=&note_text=")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)

        mockMvc.perform(request)
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(status().isOk)
    }
}