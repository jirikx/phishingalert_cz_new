package cz.phishingalert.scraper

data class ScrapingRequest(
    val domain: String,
    val accidentId: String,
    val dateTime: String) {
}