package cz.phishingalert.scraper.exporters

import cz.phishingalert.scraper.domain.Entity
import org.springframework.stereotype.Component

/**
 * Take care of exporting given entities into the output stream (printing them)
 */
@Component
class OutputStreamExporter : Exporter<Entity<*>> {
    override fun export(entity: Entity<*>) {
        println(entity)
    }
}