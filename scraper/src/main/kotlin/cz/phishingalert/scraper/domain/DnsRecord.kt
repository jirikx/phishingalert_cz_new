package cz.phishingalert.scraper.domain

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object DnsRecords : IntIdTable() {
    val name = varchar("name", 30)
    val type = integer("type")  //type id (decimal)
    val ipAddress = varchar("ip_address", 50)
    val timeToLive = long("ttl").nullable()
    val priority = integer("priority").nullable()
    val website = reference("website_id", Websites)
}

data class DnsRecord(
    override var id: Int?,
    var name: String,
    var type: Int,  //type id (decimal)
    var ipAddress: String,
    var timeToLive: Long?,
    var priority: Int? = 0,
    var websiteId: Int = 0
) : Model<Int>

object DnsRecordConverter : RowConverter<DnsRecord> {
    override fun rowToRecord(row: ResultRow): DnsRecord = DnsRecords.rowToRecord(row)
}

fun DnsRecords.rowToRecord(row: ResultRow): DnsRecord =
    DnsRecord(row[id].value, row[name], row[type], row[ipAddress], row[timeToLive], row[priority])