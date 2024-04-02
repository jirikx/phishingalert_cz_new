package cz.phishingalert.scraper.downloaders.parsers.rdap

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val eventAction: String,
    val eventDate: String
)
