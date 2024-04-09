package cz.phishingalert.scraper.domain.converters

import cz.phishingalert.scraper.domain.SslCertificate
import cz.phishingalert.scraper.domain.SslCertificates
import org.jetbrains.exposed.sql.ResultRow

object SslCertificateConverter : RowConverter<SslCertificate> {
    override fun rowToRecord(row: ResultRow): SslCertificate = SslCertificates.rowToRecord(row)
}

fun SslCertificates.rowToRecord(row: ResultRow): SslCertificate =
    SslCertificate(
        row[id].value,
        row[thumbprint],
        row[version],
        row[serialNumber],
        row[signAlgorithm],
        row[issuer],
        row[issueDate],
        row[expirationDate],
        row[subject],
        row[publicKey],
        row[issuerId],
        row[subjectId],
        row[signature]
    )