package cz.phishingalert.common.domain.converters

import cz.phishingalert.common.domain.PhishingAccident
import cz.phishingalert.common.domain.PhishingAccidents
import org.jetbrains.exposed.sql.ResultRow
import java.net.URL

object PhishingAccidentConverter : RowConverter<PhishingAccident> {
    override fun rowToRecord(row: ResultRow): PhishingAccident = PhishingAccidents.rowToRecord(row)
}

fun PhishingAccidents.rowToRecord(row: ResultRow): PhishingAccident =
    PhishingAccident(
        row[id].value, URL(row[url]), row[sentDate], row[confirmed], row[noteText],
        row[sourceEmail], row[sourcePhoneNumber], row[author].value, row[website]?.value,
        row[guid]
    )