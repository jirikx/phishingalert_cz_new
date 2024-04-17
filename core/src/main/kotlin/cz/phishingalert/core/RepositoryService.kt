package cz.phishingalert.core

import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.Authors
import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.common.domain.PhishingAccidents
import cz.phishingalert.common.repository.AuthorRepository
import cz.phishingalert.common.repository.PhishingAccidentRepository
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@Transactional
class RepositoryService(
    val authorRepository: AuthorRepository,
    val phishingAccidentRepository: PhishingAccidentRepository
) {
    fun createTablesIfNotExist() {
        // Check if the related tables were created
        if (!Authors.exists())
            SchemaUtils.create(Authors)
        if (!PhishingAccidents.exists())
            SchemaUtils.create(PhishingAccidents)
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

    fun getAllAccidents(): Collection<PhishingAccident> {
        return phishingAccidentRepository.findAll()
    }

}