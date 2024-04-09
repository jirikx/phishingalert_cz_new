package cz.phishingalert.scraper.exporters

import cz.phishingalert.scraper.domain.Model
import org.springframework.stereotype.Component

/**
 * Take care of exporting given entities into the output stream (printing them)
 */
@Component
class OutputStreamExporter : Exporter<Model<*>> {
    override fun export(model: Model<*>) {
        println(model)
    }
}