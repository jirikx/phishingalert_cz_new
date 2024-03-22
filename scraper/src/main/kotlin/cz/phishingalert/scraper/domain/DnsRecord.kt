package cz.phishingalert.scraper.domain

data class DnsRecord(
    override var id: Int?,
    var name: String,
    var type: Int,  //type id (decimal)
    var ipAddress: String,
    var timeToLive: Long?,
    var priority: Int? = 0
) : Entity<Int> {
}