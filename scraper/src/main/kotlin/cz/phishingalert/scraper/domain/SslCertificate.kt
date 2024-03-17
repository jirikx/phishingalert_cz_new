package cz.phishingalert.scraper.domain

import java.util.Date

class SslCertificate(
    override var id: String?,
    var version: String?,
    var serialNumber: String?,
    var signAlgorithm: String?,
    var issuer: String?,
    var issueDate: Date?,
    var expirationDate: Date?,
    var publicKey: String?,
    var authority: String?
) : Entity<String> {
}