package cz.phishingalert.scraper.domain

import java.util.Date

data class SslCertificate(
    override var id: String?,
    var version: String?,
    var serialNumber: String?,
    var signAlgorithm: String?,
    var issuer: String?,
    var issueDate: Date?,
    var expirationDate: Date?,
    var subject: String?,
    var publicKey: String?,
    var issuerId: String?,
    var subjectId: String?,
    var signature: String?
) : Entity<String> {
}