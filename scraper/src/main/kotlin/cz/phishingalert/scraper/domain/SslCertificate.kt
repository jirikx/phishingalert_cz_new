package cz.phishingalert.scraper.domain

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.Date

object SslCertificates : IntIdTable() {
    val version = varchar("version", 30)
    val serialNumber = varchar("serial_number", 30)
    val signAlgorithm = varchar("sign_algorithm", 300)
    val issuer = varchar("issuer", 300)
    val issueDate = datetime("issue_date")
    val expirationDate = datetime("expiration_date")
    val subject = varchar("subject", 300)
    val publicKey = varchar("public_key", 600)
    val issuerId = varchar("issuer_id", 300)
    val subjectId = varchar("subject_id", 300)
    val signature = varchar("signature", 300)
}

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
) : Model<String> {
}