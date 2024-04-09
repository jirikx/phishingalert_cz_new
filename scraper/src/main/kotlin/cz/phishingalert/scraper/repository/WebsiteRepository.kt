package cz.phishingalert.scraper.repository

import cz.phishingalert.scraper.domain.Website
import cz.phishingalert.scraper.domain.Websites
import cz.phishingalert.scraper.domain.converters.WebsiteConverter
import cz.phishingalert.scraper.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository

@Repository
class WebsiteRepository : IntTableRepository<Website, Websites>(Websites, WebsiteConverter) {
    override fun create(entity: Website): Website {
        entity.id = table.insertAndGetId {
            it[url] = entity.url.toString()
            it[domainHolder] = entity.domainHolder
            it[domainRegistrar] = entity.domainRegistrar
            it[country] = entity.country
            it[registrationDate] = entity.registrationDate
            it[lastUpdateDate] = entity.lastUpdateDate
            it[expirationDate] = entity.expirationDate
            it[filesystemPath] = entity.fileSystemPath
        }.value
        return entity
    }
}