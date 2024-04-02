package cz.phishingalert.scraper.downloaders.parsers.rdap

import kotlinx.serialization.Serializable

@Serializable
data class Entity(
    val objectClassName: String,
    val handle: String,
    val roles: List<String>
)