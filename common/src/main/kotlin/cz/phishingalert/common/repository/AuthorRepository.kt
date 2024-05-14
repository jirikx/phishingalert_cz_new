package cz.phishingalert.common.repository

import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.Authors
import cz.phishingalert.common.domain.converters.AuthorConverter
import cz.phishingalert.common.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
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

    fun findAllByEmail(email: String): List<Author> {
        val rows = table.selectAll()
            .where { table.email eq email }
            .toList()

        val result = mutableListOf<Author>()
        for (row in rows)
            result.add(AuthorConverter.rowToRecord(row))

        return result
    }
}