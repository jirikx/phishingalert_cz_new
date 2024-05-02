package cz.phishingalert.core

import cz.phishingalert.common.domain.*
import cz.phishingalert.common.repository.*
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap

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

    /**
     * Get accidents which share some of the modules with [accident] and rank them by their shared module count
     */
    fun getSimilarAccidents(accident: PhishingAccident): Map<PhishingAccident, Set<ModuleInfo>> {
        if (accident.websiteId == null)
            return emptyMap()
        val website = websiteRepository.find(accident.websiteId!!) ?: return emptyMap()

        // Get number of the shared modules for each website
        val websitesBySharedModulesCount = WebsiteModuleInfos
            .select(WebsiteModuleInfos.websiteId, WebsiteModuleInfos.moduleInfoId.countDistinct())
            .where { WebsiteModuleInfos.websiteId neq website.id }
            .andWhere { WebsiteModuleInfos.moduleInfoId inSubQuery WebsiteModuleInfos
                .select(WebsiteModuleInfos.moduleInfoId)
                .where { WebsiteModuleInfos.websiteId eq website.id }
            }
            .groupBy(WebsiteModuleInfos.websiteId)
            .map { it[WebsiteModuleInfos.websiteId].value to it[WebsiteModuleInfos.moduleInfoId.countDistinct()] }
            .sortedByDescending { it.second }
            .map { it.first }

        // Iterate through website IDs and map them to accident and module info
        val result = mutableMapOf<PhishingAccident, MutableSet<ModuleInfo>>()   // Mutable map preserves the entry order
        for (websiteId in websitesBySharedModulesCount) {
            val foundAccident = phishingAccidentRepository.findByWebsiteId(websiteId)
            if (foundAccident != null) {
                val moduleInfos = moduleInfoRepository.findAllByWebsiteId(websiteId)
                for (moduleInfo in moduleInfos)
                    result.getOrPut(foundAccident) { mutableSetOf() }.add(moduleInfo)
            }
        }

        return result
    }

    fun readAllAccidents(): Collection<PhishingAccident> =
        phishingAccidentRepository.findAll()

    fun readAccidentById(id: Int): PhishingAccident? =
        phishingAccidentRepository.find(id)

    fun readWebsiteById(id: Int): Website? =
        websiteRepository.find(id)

    fun readDnsRecordsByWebsiteId(websiteId: Int): Collection<DnsRecord> =
        dnsRepository.findAllByWebsiteId(websiteId)

    fun readModuleInfosByAccidentId(accidentId: Int): Collection<ModuleInfo> {
        val accident = readAccidentById(accidentId)
        if (accident?.websiteId == null)
            return emptyList()

        val website = readWebsiteById(accident.websiteId!!)
        if (website?.id == null)
            return emptyList()

        return readModuleInfosByWebsiteId(website.id!!)
    }

    fun readModuleInfosByWebsiteId(websiteId: Int): Collection<ModuleInfo> =
        moduleInfoRepository.findAllByWebsiteId(websiteId).sortedBy { it.name }

    fun readSslCertificatesByWebsiteId(websiteId: Int): Collection<SslCertificate> =
        sslCertificateRepository.findAllByWebsiteId(websiteId)
}