package cz.phishingalert.common.domain

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object SslCertificates : IntIdTable() {
    val thumbprint = varchar("thumbprint", 300)
    val version = varchar("version", 30)
    val serialNumber = varchar("serial_number", 30)
    val signAlgorithm = varchar("sign_algorithm", 300)
    val issuer = varchar("issuer", 300)
    val issueDate = datetime("issue_date")
    val expirationDate = datetime("expiration_date")
    val subject = varchar("subject", 300)
    val publicKey = varchar("public_key", 600)
    val issuerId = varchar("issuer_id", 300).nullable()
    val subjectId = varchar("subject_id", 300).nullable()
    val signature = varchar("signature", 300)
    val website = reference("website_id", Websites)
}

data class SslCertificate(
    override var id: Int?,
    var thumbprint: String,
    var version: String,
    var serialNumber: String,
    var signAlgorithm: String,
    var issuer: String,
    var issueDate: LocalDateTime,
    var expirationDate: LocalDateTime,
    var subject: String,
    var publicKey: String,
    var issuerId: String?,
    var subjectId: String?,
    var signature: String,
    var websiteId: Int? = 0
) : Model<Int>