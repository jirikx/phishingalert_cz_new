package cz.phishingalert.core.controllers

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class ErrorPageController : ErrorController {

    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest, model: Model): String {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)

        if (status != null) {
            val message = when (val statusCode = Integer.valueOf(status.toString())) {
                HttpStatus.NOT_FOUND.value() -> "HTTP 404 - Page not found"
                HttpStatus.INTERNAL_SERVER_ERROR.value() -> "HTTP 500 - Internal server error"
                else -> "HTTP $statusCode - ${request.getAttribute(RequestDispatcher.ERROR_MESSAGE)}"
            }

            model.addAttribute("message", message)
            return "error/errorPage"
        }

        model.addAttribute("message", "Unknown error")
        return "error/errorPage"
    }
}