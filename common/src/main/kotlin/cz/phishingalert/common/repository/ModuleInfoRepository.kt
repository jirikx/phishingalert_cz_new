package cz.phishingalert.common.repository

import cz.phishingalert.common.domain.*
import cz.phishingalert.common.domain.converters.ModuleInfoConverter
import cz.phishingalert.common.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository

@Repository
class ModuleInfoRepository : IntTableRepository<ModuleInfo, ModuleInfos>(ModuleInfos, ModuleInfoConverter) {
    override fun create(entity: ModuleInfo): ModuleInfo? {
        // Don't create anything if the entity already exists
        if (find(entity) != null)
            throw IllegalArgumentException("Can't create duplicity row in ModuleInfos table!")

        entity.id = table.insertAndGetId {
            it[name] = entity.name
            it[type] = entity.type
            it[version] = entity.version
        }.value
        return entity
    }

    /**
     * Try to find already existing entity
     */
    fun find(entity: ModuleInfo): ModuleInfo? {
        val row = table.selectAll().where {
            (table.name eq entity.name) and (table.type eq entity.type) and (table.version eq entity.version)
        }.toList()

        return if (row.isNotEmpty())
            ModuleInfoConverter.rowToRecord(row.first())
        else
            null
    }

    fun findAllByWebsiteId(websiteId: Int): Collection<ModuleInfo> {
        return ModuleInfos
            .join(WebsiteModuleInfos, JoinType.INNER, additionalConstraint = {
                ModuleInfos.id eq WebsiteModuleInfos.moduleInfoId
            })
            .selectAll()
            .where { WebsiteModuleInfos.websiteId eq websiteId }
            .map { converter.rowToRecord(it) }
    }
}