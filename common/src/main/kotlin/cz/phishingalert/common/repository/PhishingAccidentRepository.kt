package cz.phishingalert.common.repository

import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.common.domain.PhishingAccidents
import cz.phishingalert.common.domain.converters.PhishingAccidentConverter
import cz.phishingalert.common.repository.generic.IntTableRepository
import org.jetbrains.exposed.sql.insertAndGetId
import org.springframework.stereotype.Repository

@Repository
class PhishingAccidentRepository :
    IntTableRepository<PhishingAccident, PhishingAccidents>(PhishingAccidents, PhishingAccidentConverter) {
    override fun create(entity: PhishingAccident): PhishingAccident {
        entity.id = table.insertAndGetId {
            it[sentDate] = entity.sentDate
            it[confirmed] = entity.confirmed
            it[noteText] = entity.noteText
            it[sourceEmail] = entity.sourceEmail
            it[sourcePhoneNumber] = entity.sourcePhoneNumber
            it[author] = entity.authorId
        }.value
        return entity
    }
}