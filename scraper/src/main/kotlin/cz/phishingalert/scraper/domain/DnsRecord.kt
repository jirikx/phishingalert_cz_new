package cz.phishingalert.scraper.domain

data class DnsRecord(
    override var id: Int?,
    var name: String,
    var type: String,
    var ipAddress: String,
    var timeToLive: Int?,
    var priority: Int?
) : Entity<Int> {
}