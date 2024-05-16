package cz.phishingalert.scraper.downloaders.parsers

import cz.phishingalert.common.domain.Website
import cz.phishingalert.scraper.downloaders.parsers.rdap.Entity
import cz.phishingalert.scraper.downloaders.parsers.rdap.Event
import cz.phishingalert.scraper.utils.DateParser
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object WebsiteInfoParser {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Parse the raw WhoIs query result and save it into the related class
     */
    fun parseWhoIs(rawWhoIs: String): Website {
        val options = setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE)

        // Regexes for given keywords in the WhoIs query
        val registrar = Regex("^registrar:\\s*(.*)$", options).find(rawWhoIs)
        val holder = Regex("^registrant.*:\\s*(.*)$", options).find(rawWhoIs)
        val country = Regex("\\w{3,}:\\s*([a-z]{2,3})$", options).find(rawWhoIs)  // the country code is 2 or 3 characters long
        val registered = Regex("^(?:creat|register|registration|regdate)[^:]*:\\s*(.*)$", options).find(rawWhoIs)
        val updated = Regex("^(?:updat|chang|last mod)[^:]*:\\s*(.*)$", options).find(rawWhoIs)
        val expires = Regex("expir[^:]*:\\s*(.*)\$", options).find(rawWhoIs)

        val website = Website(
            null,
            (holder?.groups?.get(1)?.value ?: "unknown"),
            (registrar?.groups?.get(1)?.value ?: "unknown"),
            (country?.groups?.get(1)?.value ?: "unknown"),
            DateParser.parse(registered?.groups?.get(1)?.value),
            DateParser.parse(updated?.groups?.get(1)?.value),
            DateParser.parse(expires?.groups?.get(1)?.value)
        )

        return website
    }


    /**
     * Parse the RDAP response to the related class
     * This is more reliable than the WhoIs parsing because the RDAP record structure is actually standardized
     */
    fun parseRDAP(rawResponse: String): Website? {
        val json = Json {
            ignoreUnknownKeys = true
        }
        val jsonResponse = try {
            json.parseToJsonElement(rawResponse).jsonObject
        } catch (ex: SerializationException) {
            logger.warn("Can't serialize response from RDAP: ${ex.message}")
            return null
        }
        val website = Website()

        // Parse the registrar and registrant (holder)
        val entitiesJson = jsonResponse["entities"]?.jsonArray
        if (entitiesJson != null) {
            try {
                val entities = json.decodeFromJsonElement<List<Entity>>(entitiesJson)
                for (e in entities)
                    for (r in e.roles)
                        when (r) {
                            "registrar" -> website.domainRegistrar = e.handle
                            "registrant" -> website.domainHolder = e.handle
                        }
            } catch (ex: SerializationException) {
                logger.warn("Missing field in the RDAP JSON, parsing aborted")
                return null
            }
        } else {
            logger.warn("Missing entities in the RDAP JSON, parsing aborted")
            return null
        }

        // Parse the dates
        val eventsJson = jsonResponse["events"]?.jsonArray
        if (eventsJson != null) {
            val events = json.decodeFromJsonElement<List<Event>>(eventsJson)
            for (e in events)
                when (e.eventAction) {
                    "registration" -> website.registrationDate = DateParser.parse(e.eventDate)
                    "expiration" -> website.expirationDate = DateParser.parse(e.eventDate)
                    "last changed" -> website.lastUpdateDate = DateParser.parse(e.eventDate)
                }
        } else {
            logger.warn("Missing events in the RDAP JSON, parsing aborted")
            return null
        }

        // Use the domain name in LDH form ("ldhName") if the registrant wasn't written in the "entities" field
        if (website.domainHolder == "unknown") {
            website.domainHolder = jsonResponse["ldhName"]?.toString() ?: "unknown"
        }

        return website
    }
}