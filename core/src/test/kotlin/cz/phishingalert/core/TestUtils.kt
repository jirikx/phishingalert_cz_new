package cz.phishingalert.core

import cz.phishingalert.common.domain.*
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

class TestUtils {
    companion object {
        fun createSampleAuthor(
            id: Int = 0,
            name: String = "Jan",
            email: String = "jan@email.cz",
            userAgent: String = "UA",
            ipAddress: String = "127.0.0.1"
        ) = Author(id, name, email, userAgent, ipAddress)

        fun createSampleAccident(
            id: Int = 0,
            url: String = "https://google.com",
            sentDate: LocalDateTime = LocalDateTime.of(2020, 2, 2, 12, 0),
            confirmed: Boolean = false,
            noteText: String = "note",
            sourceEmail: String = "weird@mail.cz",
            sourcePhoneNumber: String? = null,
            authorId: Int = 0,
            websiteId: Int? = null
        ) = PhishingAccident(
            id,
            URL(url),
            sentDate,
            confirmed,
            noteText,
            sourceEmail,
            sourcePhoneNumber,
            authorId,
            websiteId
        )

        fun createSampleMessageInfo(
            id: Int = 0,
            name: String = "JavaScript++",
            type: ModuleType = ModuleType.LIBRARY,
            version: String = "1.4.7"
        ) = ModuleInfo(id, name, type, version)

        fun createSampleWebsite(
            id: Int = 0,
            domainHolder: String = "company",
            domainRegistrar: String = "Some Registar",
            country: String = "US",
            registrationDate: LocalDate = LocalDate.EPOCH,
            lastUpdateDate: LocalDate = LocalDate.of(1998, 4, 10),
            expirationDate: LocalDate = LocalDate.EPOCH,
            fileSystemPath: String = "some/fs/path"
        ) = Website(id, domainHolder, domainRegistrar, country, registrationDate, lastUpdateDate, expirationDate, fileSystemPath)

    }
}