package cz.phishingalert.scraper.domain

import java.util.*

class PhishingAccident(
    override var id: Int?,
    var sentDate: Date?,
    var confirmed: Boolean,
    var noteText: String?,
    var sourceEmail: String?,
    var sourcePhoneNumber: String?
) : Model<Int> {
}