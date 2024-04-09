package cz.phishingalert.scraper.domain.converters

import cz.phishingalert.scraper.domain.DnsRecord
import cz.phishingalert.scraper.domain.DnsRecords
import org.jetbrains.exposed.sql.ResultRow

object DnsRecordConverter : RowConverter<DnsRecord> {
    override fun rowToRecord(row: ResultRow): DnsRecord = DnsRecords.rowToRecord(row)
}

fun DnsRecords.rowToRecord(row: ResultRow): DnsRecord =
    DnsRecord(row[id].value, row[name], row[type], row[ipAddress], row[timeToLive], row[priority])