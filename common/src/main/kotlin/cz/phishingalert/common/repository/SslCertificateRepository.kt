package cz.phishingalert.common.repository

import cz.phishingalert.common.domain.DnsRecord
import cz.phishingalert.common.domain.SslCertificate
import cz.phishingalert.common.domain.converters.SslCertificateConverter
import cz.phishingalert.common.domain.SslCertificates
import cz.phishingalert.common.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository

@Repository
class SslCertificateRepository :
    IntTableRepository<SslCertificate, SslCertificates>(SslCertificates, SslCertificateConverter) {
    override fun create(entity: SslCertificate): SslCertificate? {
        entity.id = table.insertAndGetId {
            it[thumbprint] = entity.thumbprint
            it[version] = entity.version
            it[serialNumber] = entity.serialNumber
            it[signAlgorithm] = entity.signAlgorithm
            it[issuer] = entity.issuer
            it[issueDate] = entity.issueDate
            it[expirationDate] = entity.expirationDate
            it[subject] = entity.subject
            it[publicKey] = entity.publicKey
            it[issuerId] = entity.issuerId
            it[subjectId] = entity.subjectId
            it[signature] = entity.signature
            it[website] = entity.websiteId
        }.value
        return entity
    }

    fun findAllByWebsiteId(websiteId: Int): Collection<SslCertificate> {
        return table
            .selectAll()
            .where { table.website eq websiteId }
            .map { converter.rowToRecord(it) }
    }
}