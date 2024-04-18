package cz.phishingalert.core.authorities

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class CloudflareController {
    val submitUrl = "https://abuse.cloudflare.com/phishing"

    /**
     * Sends the info about given accident identified by [accidentId] to the [authority] such as Cloudflare or Google
     */
    @GetMapping(path = ["/submit/cloudflare/{accidentId}"])
    fun sendAccidentToAuthority(
        @PathVariable accidentId: Int
    ): ModelAndView {
        return ModelAndView("redirect:$submitUrl")
    }

    fun buildQueryString() {
        
    }

    fun addQueryParameter(key: String, value: String): String = "?$key=$value"
}