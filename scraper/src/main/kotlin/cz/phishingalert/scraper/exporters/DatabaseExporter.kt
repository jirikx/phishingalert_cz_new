package cz.phishingalert.scraper.exporters

import cz.phishingalert.common.domain.*
import cz.phishingalert.common.repository.DnsRecordRepository
import cz.phishingalert.common.repository.ModuleInfoRepository
import cz.phishingalert.common.repository.SslCertificateRepository
import cz.phishingalert.common.repository.WebsiteRepository
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
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
        createTablesIfNotExist()
        val insertedWebsite = websiteRepository.create(website)

        for (dnsRecord in dnsRecords) {
            dnsRecord.websiteId = insertedWebsite?.id!!
            dnsRecordRepository.create(dnsRecord)
        }

        for (module in modules) {
            val databaseModule = moduleInfoRepository.find(module) ?: moduleInfoRepository.create(module)
            WebsiteModuleInfos.insert {
                it[websiteId] = insertedWebsite?.id!!
                it[moduleInfoId] = databaseModule?.id!!
            }

        }

        for (cert in certs) {
            cert.websiteId = insertedWebsite?.id!!
            sslCertificateRepository.create(cert)
        }

    }

    /**
     * Create the database tables if they don't exist.
     * This function needs to be used because Exposed library only scans tables in current module by default
     * and so it doesn't create tables from different Gradle modules.
     */
    fun createTablesIfNotExist() {
        if (!Websites.exists())
            SchemaUtils.create(Websites)
        if ((!DnsRecords.exists()))
            SchemaUtils.create(DnsRecords)
        if(!ModuleInfos.exists())
            SchemaUtils.create(ModuleInfos)
        if (!SslCertificates.exists())
            SchemaUtils.create(SslCertificates)
        if (!WebsiteModuleInfos.exists())
            SchemaUtils.create(WebsiteModuleInfos)
    }
}