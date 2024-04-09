package cz.phishingalert.scraper.exporters

import cz.phishingalert.scraper.domain.*
import cz.phishingalert.scraper.repository.DnsRecordRepository
import cz.phishingalert.scraper.repository.ModuleInfoRepository
import cz.phishingalert.scraper.repository.SslCertificateRepository
import cz.phishingalert.scraper.repository.WebsiteRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Take care of exporting given entities into the database
 */
@Transactional
@Component
class DatabaseExporter(
    val websiteRepository: WebsiteRepository,
    val dnsRecordRepository: DnsRecordRepository,
    val moduleInfoRepository: ModuleInfoRepository,
    val sslCertificateRepository: SslCertificateRepository
) : Exporter {
    override fun export(
        website: Website,
        dnsRecords: Collection<DnsRecord>,
        modules: Collection<ModuleInfo>,
        certs: Collection<SslCertificate>
    ) {
        val insertedWebsite = websiteRepository.create(website)

        for (dnsRecord in dnsRecords) {
            dnsRecord.websiteId = insertedWebsite.id!!
            dnsRecordRepository.create(dnsRecord)
        }

        for (module in modules) {
            val moduleId = moduleInfoRepository.create(module)
            WebsiteModuleInfos.insert {
                it[websiteId] = insertedWebsite.id!!
                it[moduleInfoId] = moduleId.id!!
            }

        }

        for (cert in certs) {
            cert.websiteId = insertedWebsite.id
            sslCertificateRepository.create(cert)
        }

    }
}