package cz.phishingalert.common.messagequeue

import com.fasterxml.jackson.annotation.JsonProperty

const val QUEUE_NAME = "scraperQueue"

/**
 * Wrapper for messages which are send between core and scraper
 */
data class ScrapingMessage(
    @JsonProperty("accidentId") val accidentId: Int,
    @JsonProperty("url") val url: String,
    @JsonProperty("dateTime") val dateTime: String,
    @JsonProperty("shouldCrawl") val shouldCrawl: Boolean)