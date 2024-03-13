package cz.phishingalert.scraper.exporters

interface Exporter<T> {
    fun export(entity: T): Unit
}