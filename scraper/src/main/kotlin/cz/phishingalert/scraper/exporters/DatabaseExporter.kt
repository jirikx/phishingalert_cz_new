package cz.phishingalert.scraper.exporters

import cz.phishingalert.scraper.domain.Entity

/**
 * Take care of exporting given entities into the database
 */
class DatabaseExporter : Exporter<Entity<*>> {
    override fun export(entity: Entity<*>) {
        TODO("Not yet implemented")
    }
}