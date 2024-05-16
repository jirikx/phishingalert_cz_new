package cz.phishingalert.core.controllers.admin

import cz.phishingalert.core.services.RepositoryService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
@Controller
@RequestMapping(path = ["/admin"])
class AdminController(val repositoryService: RepositoryService) {
    @GetMapping
    fun showAdminPanel(model: Model): String {
        val accidents = repositoryService.readAllAccidents().sortedBy { it.id }
        model.addAttribute("accidents", accidents)

        return "admin/admin"
    }

}

