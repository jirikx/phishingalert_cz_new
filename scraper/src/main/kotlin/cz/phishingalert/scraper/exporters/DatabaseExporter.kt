package cz.phishingalert.scraper.exporters

import cz.phishingalert.scraper.domain.Model

/**
 * Take care of exporting given entities into the database
 */
class DatabaseExporter : Exporter<Model<*>> {
    override fun export(model: Model<*>) {
        TODO("Not yet implemented")
    }
}