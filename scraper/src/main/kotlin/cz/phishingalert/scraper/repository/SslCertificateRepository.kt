package cz.phishingalert.scraper.repository

import cz.phishingalert.scraper.domain.SslCertificate
import cz.phishingalert.scraper.domain.SslCertificateConverter
import cz.phishingalert.scraper.domain.SslCertificates
import cz.phishingalert.scraper.repository.generic.IntTableRepository
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