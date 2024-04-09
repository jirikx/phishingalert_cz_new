package cz.phishingalert.scraper.domain

class Author(
    override var id: Int?,
    var name: String?,
    var email: String?,
    var userAgent: String?,
    var ipAddress: String?
) : Model<Int>