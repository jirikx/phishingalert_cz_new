package cz.phishingalert.core.controllers.admin.reporting

import cz.phishingalert.core.RepositoryService
import cz.phishingalert.core.configuration.ReportAuthor
import cz.phishingalert.core.reportbuilders.QueryStringReportBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping(path = ["/admin"])
class CloudflareController(
    val reportAuthor: ReportAuthor,
    val repositoryService: RepositoryService) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    val submitUrl = "https://abuse.cloudflare.com/phishing"

    /**
     * Sends the info about given accident identified by [accidentId] to the [authority] such as Cloudflare or Google
     */
    @GetMapping(path = ["/submit/cloudflare/{accidentId}"])
    fun sendAccidentToAuthority(
        @PathVariable accidentId: Int
    ): ModelAndView {
        val queryString = createQueryString(accidentId)

        if (queryString == null) {
            logger.warn("Can't report phishing accident with $accidentId because it wasn't found!")
            return ModelAndView("error/404", HttpStatus.NOT_FOUND)
        }

        return ModelAndView("redirect:$submitUrl$queryString")
    }

    /**
     * Create query string filled with data about accident which is identified by [accidentId]
     */
    fun createQueryString(accidentId: Int): String? {
        val queryStringReportBuilder = QueryStringReportBuilder()

        val accident = repositoryService.readAccidentById(accidentId)
        if (accident == null) {
            logger.error("Phishing accident with id=$accidentId couldn't be found!")
            return null
        }

        addAuthorToQuery(queryStringReportBuilder)
        queryStringReportBuilder.addParameter("urls", accident.url.toString())
        if (accident.noteText != null)
            queryStringReportBuilder.addParameter("comments", accident.noteText!!)
        if (accident.sourceEmail != null && accident.sourceEmail!!.isNotBlank())
            queryStringReportBuilder.addParameter("justification", "Came from email: ${accident.sourceEmail!!}")
        if (accident.sourcePhoneNumber != null && accident.sourcePhoneNumber!!.isNotBlank())
            queryStringReportBuilder.addParameter("justification", "It was sent via phone number: ${accident.sourcePhoneNumber!!}")

        // Utilise website's data if it exists
        val website = repositoryService.readWebsiteById(accident.websiteId!!) ?: return queryStringReportBuilder.getQuery()
        queryStringReportBuilder.addParameter(
            "justification",
            "The website was registered at ${website.domainRegistrar} registrar and the domain holder is ${website.domainHolder}.")

        // Fill the info about modules
        val modules = repositoryService.readModuleInfosByWebsiteId(website.id!!)
        if (modules.isNotEmpty()) {
            queryStringReportBuilder.addParameter(
                "justification",
                "It uses tech stack which consists of ${modules.joinToString(separator = ", ") { it.name }}.")
        }

        return queryStringReportBuilder.getQuery()
    }

    fun addAuthorToQuery(queryStringReportBuilder: QueryStringReportBuilder) {
        queryStringReportBuilder.addParameter("name", reportAuthor.name)
        queryStringReportBuilder.addParameter("email", reportAuthor.email)
        queryStringReportBuilder.addParameter("email2", reportAuthor.email)

        if (reportAuthor.company != null)
            queryStringReportBuilder.addParameter("company", reportAuthor.company!!)

        if (reportAuthor.phoneNumber != null)
            queryStringReportBuilder.addParameter("telephone", reportAuthor.phoneNumber!!)
    }

}