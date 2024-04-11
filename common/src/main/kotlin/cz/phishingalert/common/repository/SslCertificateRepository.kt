package cz.phishingalert.common.repository

import cz.phishingalert.common.domain.SslCertificate
import cz.phishingalert.common.domain.converters.SslCertificateConverter
import cz.phishingalert.common.domain.SslCertificates
import cz.phishingalert.common.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Repository

@Repository
class SslCertificateRepository :
    IntTableRepository<SslCertificate, SslCertificates>(SslCertificates, SslCertificateConverter) {
    override fun create(entity: SslCertificate): SslCertificate {
        entity.id = table.insertAndGetId {
            it[thumbprint]
            it[version]
            it[serialNumber]
            it[signAlgorithm]
            it[issuer]
            it[issueDate]
            it[expirationDate]
            it[subject]
            it[publicKey]
            it[issuerId]
            it[subjectId]
            it[signature]
        }.value
        return entity
    }
}