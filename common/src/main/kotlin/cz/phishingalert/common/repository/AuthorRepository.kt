package cz.phishingalert.common.repository

import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.Authors
import cz.phishingalert.common.domain.converters.AuthorConverter
import cz.phishingalert.common.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository : IntTableRepository<Author, Authors>(Authors, AuthorConverter) {
    override fun create(entity: Author): Author? {
        entity.id = table.insertAndGetId {
            it[name] = entity.name
            it[email] = entity.email
            it[userAgent] = entity.userAgent
            it[ipAddress] = entity.ipAddress
        }.value
        return entity
    }
}