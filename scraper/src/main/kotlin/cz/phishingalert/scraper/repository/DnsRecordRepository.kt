package cz.phishingalert.scraper.repository

import cz.phishingalert.scraper.domain.DnsRecord
import cz.phishingalert.scraper.domain.DnsRecordConverter
import cz.phishingalert.scraper.domain.DnsRecords
import cz.phishingalert.scraper.domain.RowConverter
import cz.phishingalert.scraper.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Repository

@Repository
class DnsRecordRepository : IntTableRepository<DnsRecord, DnsRecords>(DnsRecords, DnsRecordConverter) {
    override fun create(entity: DnsRecord): DnsRecord {
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