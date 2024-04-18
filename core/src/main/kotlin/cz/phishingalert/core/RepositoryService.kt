package cz.phishingalert.core

import cz.phishingalert.common.domain.*
import cz.phishingalert.common.repository.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@Transactional
class RepositoryService(
    val authorRepository: AuthorRepository,
    val dnsRepository: DnsRecordRepository,
    val moduleInfoRepository: ModuleInfoRepository,
    val phishingAccidentRepository: PhishingAccidentRepository,
    val sslCertificateRepository: SslCertificateRepository,
    val websiteRepository: WebsiteRepository
) {
    fun createTablesIfNotExist() {
        // Check if the related tables were created
        if (!Authors.exists())
            SchemaUtils.create(Authors)
        if (!PhishingAccidents.exists())
            SchemaUtils.create(PhishingAccidents)
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

    /**
     * Save given [author] and [accident] to the database
     * @return id of saved [accident], null if the [accident] couldn't be saved
     */
    fun save(author: Author, accident: PhishingAccident): Int? {
        createTablesIfNotExist()
        val insertedAuthor = authorRepository.create(author) ?: return null
        accident.authorId = insertedAuthor.id!!

        val insertedAccident = phishingAccidentRepository.create(accident) ?: return null
        return insertedAccident.id
    }

    fun timeOfLastSimilarAccident(accident: PhishingAccident): LocalDateTime {
        createTablesIfNotExist()
        val lastSimilarAccident = phishingAccidentRepository.findNewestByUrl(accident) ?: return LocalDateTime.MIN

        return lastSimilarAccident.sentDate
    }

    fun readAllAccidents(): Collection<PhishingAccident> =
        phishingAccidentRepository.findAll()

    fun readAccidentById(id: Int): PhishingAccident? =
        phishingAccidentRepository.find(id)

    fun readWebsiteById(id:Int): Website? =
        websiteRepository.find(id)

    fun readDnsRecordsByWebsiteId(websiteId: Int): Collection<DnsRecord> =
        dnsRepository.findAllByWebsiteId(websiteId)

    fun readModuleInfosByWebsiteId(websiteId: Int): Collection<ModuleInfo> =
        moduleInfoRepository.findAllByWebsiteId(websiteId)

    fun readSslCertificatesByWebsiteId(websiteId: Int): Collection<SslCertificate> =
        sslCertificateRepository.findAllByWebsiteId(websiteId)
}