package cz.phishingalert.scraper.repository

import cz.phishingalert.scraper.domain.Website
import cz.phishingalert.scraper.domain.Websites
import cz.phishingalert.scraper.domain.rowToRecord
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.stereotype.Repository

@Repository
class WebsiteRepository : CrudRepository<Website, Int> {
    val table: IdTable<Int> = Websites

    override fun create(entity: Website): Website {
        entity.id = Websites.insertAndGetId {
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

    override fun find(id: Int): Website? {
        val w = Websites.selectAll().where { Websites.id eq id }.singleOrNull()
        return if (w != null)
            Websites.rowToRecord(w)
        else
            null
    }

    override fun findAll(): Collection<Website> {
        return Websites.selectAll().map { Websites.rowToRecord(it) }
    }

    override fun delete(id: Int): Boolean {
        val deleted = Websites.deleteWhere { Websites.id eq id }
        return deleted != 0
    }
}