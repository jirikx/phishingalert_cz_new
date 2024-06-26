package cz.phishingalert.core

import cz.phishingalert.common.domain.*
import cz.phishingalert.common.repository.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
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

@SpringBootTest(
    classes = [TestConfig::class],
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class RepositoryTests{
    @Autowired private lateinit var authorRepository: AuthorRepository
    @Autowired private lateinit var dnsRepository: DnsRecordRepository
    @Autowired private lateinit var moduleInfoRepository: ModuleInfoRepository
    @Autowired private lateinit var phishingAccidentRepository: PhishingAccidentRepository
    @Autowired private lateinit var sslCertificateRepository: SslCertificateRepository
    @Autowired private lateinit var websiteRepository: WebsiteRepository

    @BeforeEach
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "org.h2.Driver", "sa", "password")
        transaction {
            SchemaUtils.dropDatabase()
            SchemaUtils.create(Authors)
            SchemaUtils.create(PhishingAccidents)
            SchemaUtils.create(Websites)
            SchemaUtils.create(DnsRecords)
            SchemaUtils.create(ModuleInfos)
            SchemaUtils.create(SslCertificates)
            SchemaUtils.create(WebsiteModuleInfos)
        }
    }

    @AfterEach
    fun cleanup() {
        transaction {
            SchemaUtils.dropSchema()
        }
    }

    @Test
    fun createManyToManyRelationBetweenWebsiteAndModuleInfo() {
        val website = TestUtils.createSampleWebsite()
        val ctu = TestUtils.createSampleWebsite(domainRegistrar = "CTU in Prague")
        val library = TestUtils.createSampleMessageInfo()
        val framework = TestUtils.createSampleMessageInfo(name = "React#", type = ModuleType.FRAMEWORK)

        transaction {
            val insertedWebsite = websiteRepository.create(website)
            val insertedCtu = websiteRepository.create(ctu)
            val insertedLibrary = moduleInfoRepository.create(library)
            val insertedFramework = moduleInfoRepository.create(framework)

            WebsiteModuleInfos.insert {
                it[websiteId] = insertedWebsite!!.id!!
                it[moduleInfoId] = insertedLibrary!!.id!!
            }
            WebsiteModuleInfos.insert {
                it[websiteId] = insertedWebsite!!.id!!
                it[moduleInfoId] = insertedFramework!!.id!!
            }
            WebsiteModuleInfos.insert {
                it[websiteId] = insertedCtu!!.id!!
                it[moduleInfoId] = insertedLibrary!!.id!!
            }


        }
    }

    @Test
    fun createJustAuthorTest() {
        val author = TestUtils.createSampleAuthor()

        transaction {
            SchemaUtils.create(Authors)
            val inserted = authorRepository.create(author)
            val res = authorRepository.findAll()
            author.id = inserted!!.id

            assert(res.isNotEmpty())
            assertEquals(author, inserted)
        }
    }

    @Test
    fun createPhishingAccidentTest() {
        val author = TestUtils.createSampleAuthor()

        transaction {
            val insertedAuthor = authorRepository.create(author)
            assertNotNull(insertedAuthor)

            val accident = TestUtils.createSampleAccident(authorId = insertedAuthor!!.id!!)

            val insertedAccident = phishingAccidentRepository.create(accident)
            accident.id = insertedAccident!!.id

            assertEquals(accident, insertedAccident)
        }
    }


}