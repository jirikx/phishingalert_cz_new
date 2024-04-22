package cz.phishingalert.core.controllers.admin

import cz.phishingalert.core.RepositoryService
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
    @GetMapping(path = ["/{accidentId}"])
    fun show(@PathVariable accidentId: Int, model: Model): ModelAndView {
        val accident = repositoryService.readAccidentById(accidentId)
            ?: return ModelAndView("error/404", HttpStatus.NOT_FOUND)

        model.addAttribute("websiteUrl", accident.url.toString())
        model.addAttribute(
            "similarAccidents",
            repositoryService.getSimilarAccidents(accident).sortedBy { it.id })
        return ModelAndView("admin/stats")
    }
}