package cz.phishingalert.common.domain.converters

import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.common.domain.PhishingAccidents
import org.jetbrains.exposed.sql.ResultRow

object PhishingAccidentConverter : RowConverter<PhishingAccident> {
    override fun rowToRecord(row: ResultRow): PhishingAccident = PhishingAccidents.rowToRecord(row)
}

fun PhishingAccidents.rowToRecord(row: ResultRow): PhishingAccident =
    PhishingAccident(row[id].value, row[sentDate], row[confirmed], row[noteText], row[sourceEmail], row[sourcePhoneNumber])