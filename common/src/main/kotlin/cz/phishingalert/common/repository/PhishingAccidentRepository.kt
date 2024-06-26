package cz.phishingalert.common.repository

import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.common.domain.PhishingAccidents
import cz.phishingalert.common.domain.converters.PhishingAccidentConverter
import cz.phishingalert.common.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PhishingAccidentRepository :
    IntTableRepository<PhishingAccident, PhishingAccidents>(PhishingAccidents, PhishingAccidentConverter) {
    private fun PhishingAccidents.setProperties(it: UpdateBuilder<Number>, entity: PhishingAccident) {
        it[url] = entity.url.toString()
        it[sentDate] = entity.sentDate
        it[confirmed] = entity.confirmed
        it[noteText] = entity.noteText
        it[sourceEmail] = entity.sourceEmail
        it[sourcePhoneNumber] = entity.sourcePhoneNumber
        it[author] = entity.authorId
        it[website] = entity.websiteId
        it[guid] = entity.guid
    }

    override fun create(entity: PhishingAccident): PhishingAccident? {
        entity.id = table.insertAndGetId {
            setProperties(it, entity)
        }.value
        return entity
    }

    fun update(entity: PhishingAccident): PhishingAccident? {
        if (entity.id == null)
            return null

        table.update({table.id eq entity.id}) {
            setProperties(it, entity)
        }
        return find(entity.id!!)
    }

    fun findByWebsiteId(websiteId: Int): PhishingAccident? {
        val row = table.selectAll()
            .where { table.website eq websiteId }
            .limit(1).singleOrNull()

        if (row != null)
            return PhishingAccidentConverter.rowToRecord(row)
        return null
    }

    fun findNewestByUrl(entity: PhishingAccident): PhishingAccident? {
        val row = table.selectAll()
            .where { table.url eq entity.url.toString() }
            .orderBy(table.id, SortOrder.DESC)
            .limit(1).singleOrNull()

        if (row != null)
            return PhishingAccidentConverter.rowToRecord(row)
        return null
    }

    fun findByGuid(accidentGuid: UUID): PhishingAccident? {
        val row = table.selectAll()
            .where { table.guid eq accidentGuid }
            .limit(1).singleOrNull()

        if (row != null)
            return PhishingAccidentConverter.rowToRecord(row)
        return null
    }

    fun findByAuthorId(authorId: Int): PhishingAccident? {
        val row = table.selectAll()
            .where { table.author eq authorId }
            .limit(1).singleOrNull()

        if (row != null)
            return PhishingAccidentConverter.rowToRecord(row)
        return null
    }
}