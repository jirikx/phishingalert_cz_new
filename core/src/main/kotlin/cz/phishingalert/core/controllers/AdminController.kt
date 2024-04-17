package cz.phishingalert.core.controllers

import cz.phishingalert.core.RepositoryService
import cz.phishingalert.core.authorities.Authority
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
@Controller
@RequestMapping(path = ["/admin"])
class AdminController(val repositoryService: RepositoryService) {
    /**
     * Sends the info about given accident identified by [accidentId] to the [authority] such as Cloudflare or Google
     */
    @PostMapping(path = ["/submit/{authority}/{accidentId}"])
    fun sendAccidentToAuthority(
        @PathVariable authority: Authority,
        @PathVariable accidentId: Int
    ) {

    }

    @GetMapping
    fun showAdminPanel(model: Model): String {
        val accidents = repositoryService.getAllAccidents()
        model.addAttribute("accidents", accidents)

        return "admin/admin"
    }

}

