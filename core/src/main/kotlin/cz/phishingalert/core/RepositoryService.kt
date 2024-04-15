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

@Component
@Transactional
class RepositoryService(
    val authorRepository: AuthorRepository,
    val phishingAccidentRepository: PhishingAccidentRepository
) {
    fun save(author: Author, accident: PhishingAccident) {
        // Check if the related tables were created
        if (!Authors.exists())
            SchemaUtils.create(Authors)
        if (!PhishingAccidents.exists())
            SchemaUtils.create(PhishingAccidents)

        val inserted = authorRepository.create(author)
        accident.authorId = inserted.id!!
        phishingAccidentRepository.create(accident)
    }

}