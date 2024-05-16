package cz.phishingalert.core.controllers.admin

import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.ModuleInfo
import cz.phishingalert.common.domain.Website
import cz.phishingalert.core.services.RepositoryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping(path = ["/admin/stats"])
class StatsController(
    val repositoryService: RepositoryService
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping(path = ["/{accidentId}"])
    fun show(@PathVariable accidentId: Int, model: Model): ModelAndView {
        val accident = repositoryService.readAccidentById(accidentId)
            ?: return ModelAndView("error/404", HttpStatus.NOT_FOUND)
        var website: Website? = null
        var dnsRecordNames: Collection<String> = emptyList()
        var modules: Collection<ModuleInfo> = emptyList()

        // Search for ModuleInfo and DNS records if the Website record exists
        if (accident.websiteId != null) {
            website = repositoryService.readWebsiteById(accident.websiteId!!)
            modules = repositoryService.readModuleInfosByAccidentId(accidentId)
            dnsRecordNames = repositoryService.readDnsRecordsByWebsiteId(accident.websiteId!!).map { it.name }
        }

        model.addAttribute("accident", accident)
        model.addAttribute(
            "author",
            repositoryService.readAuthorById(accident.authorId) ?: Author(0, "-" , "-", "-", "-")
        )
        model.addAttribute("repositoryService", repositoryService)

        // Add Website object
        model.addAttribute("website", website)

        // Add collection of ModuleInfo
        model.addAttribute("usedModules", modules)

        // Add collection of DNS records
        model.addAttribute("dnsRecordNames", dnsRecordNames)

        model.addAttribute(
            "similarAccidents",
            repositoryService.getSimilarAccidents(accident))
        return ModelAndView("admin/stats")
    }

    @GetMapping(path = ["/user/{email}"])
    fun accidentsFromGivenEmail(@PathVariable email: String, model: Model): ModelAndView {
        val accidents = repositoryService.readAccidentsByAuthorEmail(email)
        logger.info("Request for accidents from email: $email")

        // Handle unknown email
        if (accidents.isEmpty()) {
            model.addAttribute("message", "There are no reports for email $email")
            return ModelAndView("error/errorPage", HttpStatus.NOT_FOUND)
        }

        model.addAttribute("email", email)
        model.addAttribute("accidents", accidents)
        return ModelAndView("admin/user")
    }
}