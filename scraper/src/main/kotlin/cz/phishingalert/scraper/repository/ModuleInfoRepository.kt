package cz.phishingalert.scraper.repository

import cz.phishingalert.scraper.domain.ModuleInfo
import cz.phishingalert.scraper.domain.converters.ModuleInfoConverter
import cz.phishingalert.scraper.domain.ModuleInfos
import cz.phishingalert.scraper.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository

@Repository
class ModuleInfoRepository : IntTableRepository<ModuleInfo, ModuleInfos>(ModuleInfos, ModuleInfoConverter) {
    override fun create(entity: ModuleInfo): ModuleInfo {
        entity.id = table.insertAndGetId {
            it[name] = entity.name
            it[type] = entity.type!!
            it[version] = entity.version
        }.value
        return entity
    }

    fun findByNameAndVersion(name: String, version: String): List<ModuleInfo> {
        val row = table.selectAll().where {
            (table.name eq name) and (table.version eq version)
        }.toList()
        return if (row.isNotEmpty())
            row.map { converter.rowToRecord(it) }
        else
            emptyList()
    }
}