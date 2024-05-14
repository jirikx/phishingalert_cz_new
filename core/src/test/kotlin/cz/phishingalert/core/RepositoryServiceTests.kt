package cz.phishingalert.core

import cz.phishingalert.common.domain.ModuleType
import cz.phishingalert.common.domain.WebsiteModuleInfos
import cz.phishingalert.common.repository.*
import cz.phishingalert.core.services.RepositoryService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.event.annotation.BeforeTestMethod
import java.time.LocalDateTime

@SpringBootTest(classes = [TestConfig::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepositoryServiceTests {
    @Autowired lateinit var moduleInfoRepository: ModuleInfoRepository
    @Autowired lateinit var websiteRepository: WebsiteRepository
    @Autowired lateinit var repositoryService: RepositoryService

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "org.h2.Driver", "sa", "password")
        transaction {
            repositoryService.createTablesIfNotExist()
            commit()
        }
    }

    @AfterEach
    fun betweenTests() {
        transaction {
            SchemaUtils.dropDatabase()
            commit()
        }
    }

//    @AfterEach
//    fun cleanup() {
//        transaction {
//
//        }
//    }

    @Test
    fun addAuthorAndPhishingAccidentTest() {
        val author = TestUtils.createSampleAuthor()
        val phishingAccident = TestUtils.createSampleAccident()

        transaction {
            val accidentId = repositoryService.save(author, phishingAccident)
            phishingAccident.id = accidentId
            assertNotNull(accidentId)

            val readAccident = repositoryService.readAccidentById(accidentId!!)
            phishingAccident.authorId = readAccident!!.authorId
            assertEquals(phishingAccident, readAccident)
        }
    }

    @Test
    fun getDateOfLastSimilarAccidentTest() {
        val author = TestUtils.createSampleAuthor()
        val oldAccident = TestUtils.createSampleAccident(sentDate = LocalDateTime.of(2024, 1, 1, 1, 0))
        val newAccident = TestUtils.createSampleAccident(sentDate = LocalDateTime.of(2024, 1, 1, 1, 1))

        transaction {
            oldAccident.id = repositoryService.save(author, oldAccident)

            val lastSimilarAccidentSentDate = repositoryService.timeOfLastSimilarAccident(newAccident)
            assertEquals(oldAccident.sentDate, lastSimilarAccidentSentDate)

            println(repositoryService.readAllAccidents().size)
        }
    }

    @Test
    fun moduleInfosByWebsiteIdTest() {
        val cloudflare = TestUtils.createSampleWebsite(domainRegistrar = "Cloudflare inc.")
        val ctu = TestUtils.createSampleWebsite(domainRegistrar = "CTU in Prague")
        val library = TestUtils.createSampleMessageInfo(name = "C++")
        val framework = TestUtils.createSampleMessageInfo(name = "Vue#", type = ModuleType.FRAMEWORK)

        // Set up the test data (websites, module info) in many-to-many relation:
        //      cloudflare <-> library, framework
        //      ctu <-> library
        transaction {
            val insertedCloudflare = websiteRepository.create(cloudflare)
            val insertedCtu = websiteRepository.create(ctu)
            val insertedLibrary = moduleInfoRepository.create(library)
            val insertedFramework = moduleInfoRepository.create(framework)

            WebsiteModuleInfos.insert {
                it[websiteId] = insertedCloudflare!!.id!!
                it[moduleInfoId] = insertedLibrary!!.id!!
            }
            WebsiteModuleInfos.insert {
                it[websiteId] = insertedCloudflare!!.id!!
                it[moduleInfoId] = insertedFramework!!.id!!
            }
            WebsiteModuleInfos.insert {
                it[websiteId] = insertedCtu!!.id!!
                it[moduleInfoId] = insertedLibrary!!.id!!
            }

            val modulesOfCloudflare = repositoryService.readModuleInfosByWebsiteId(insertedCloudflare!!.id!!)
            assertEquals(2, modulesOfCloudflare.size)
            assertTrue(modulesOfCloudflare.contains(insertedLibrary))
            assertTrue(modulesOfCloudflare.contains(insertedFramework))

            val modulesOfCtu = repositoryService.readModuleInfosByWebsiteId(insertedCtu!!.id!!)
            assertEquals(1, modulesOfCtu.size)
            assertTrue(modulesOfCtu.contains(insertedLibrary))
        }
    }
}