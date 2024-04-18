package cz.phishingalert.scraper.exporters

import cz.phishingalert.common.domain.*
import cz.phishingalert.common.repository.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.exp

/**
 * Take care of exporting given entities into the database
 */
@Transactional
@Component
@Profile("message-queue")
class DatabaseExporter(
    val phishingAccidentRepository: PhishingAccidentRepository,
    val websiteRepository: WebsiteRepository,
    val dnsRecordRepository: DnsRecordRepository,
    val moduleInfoRepository: ModuleInfoRepository,
    val sslCertificateRepository: SslCertificateRepository
) : Exporter {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun export(
        website: Website,
        dnsRecords: Collection<DnsRecord>,
        modules: Collection<ModuleInfo>,
        certs: Collection<SslCertificate>
    ) {
        insertRowsFromScraper(website, dnsRecords, modules, certs)
    }

    override fun export(
        phishingAccidentId: Int,
        website: Website,
        dnsRecords: Collection<DnsRecord>,
        modules: Collection<ModuleInfo>,
        certs: Collection<SslCertificate>
    ) {
        val accident = phishingAccidentRepository.find(phishingAccidentId)
        if (accident == null) {
            logger.error("Accident with id=$phishingAccidentId wasn't found. Data export aborted!")
            return
        }

        // Connect the Accident with its Website
        val websiteId = insertRowsFromScraper(website, dnsRecords, modules, certs)
        if (websiteId == null) {
            logger.error("Accident with id=$phishingAccidentId couldn't be connected with a website. Data export aborted!")
            return
        }
        accident.websiteId = websiteId
        phishingAccidentRepository.update(accident)
    }

    /**
     * Insert the rows with data from the Scraper module
     * @return id of inserted [website] row, null if the row wasn't created
     */
    fun insertRowsFromScraper(
        website: Website,
        dnsRecords: Collection<DnsRecord>,
        modules: Collection<ModuleInfo>,
        certs: Collection<SslCertificate>
    ): Int? {
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

        return insertedWebsite?.id
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
        if (!ModuleInfos.exists())
            SchemaUtils.create(ModuleInfos)
        if (!SslCertificates.exists())
            SchemaUtils.create(SslCertificates)
        if (!WebsiteModuleInfos.exists())
            SchemaUtils.create(WebsiteModuleInfos)
    }
}