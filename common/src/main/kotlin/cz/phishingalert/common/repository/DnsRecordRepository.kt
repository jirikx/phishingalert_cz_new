package cz.phishingalert.common.repository

import cz.phishingalert.common.domain.DnsRecord
import cz.phishingalert.common.domain.converters.DnsRecordConverter
import cz.phishingalert.common.domain.DnsRecords
import cz.phishingalert.common.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Repository

@Repository
class DnsRecordRepository : IntTableRepository<DnsRecord, DnsRecords>(DnsRecords, DnsRecordConverter) {
    override fun create(entity: DnsRecord): DnsRecord? {
        entity.id = table.insertAndGetId {
            it[name] = entity.name
            it[type] = entity.type
            it[ipAddress] = entity.ipAddress
            it[timeToLive] = entity.timeToLive
            it[priority] = entity.priority
            it[website] = entity.websiteId
        }.value
        return entity
    }
}