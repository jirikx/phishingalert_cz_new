package cz.phishingalert.scraper.domain.converters

import cz.phishingalert.scraper.domain.Website
import cz.phishingalert.scraper.domain.Websites
import org.jetbrains.exposed.sql.ResultRow
import java.net.URL

object WebsiteConverter : RowConverter<Website> {
    override fun rowToRecord(row: ResultRow): Website = Websites.rowToRecord(row)
}

fun Websites.rowToRecord(row: ResultRow): Website =
    Website(
        row[id].value, URL(row[url]), row[domainHolder], row[domainRegistrar], row[country], row[registrationDate],
        row[lastUpdateDate], row[expirationDate], row[filesystemPath]
    )