package cz.phishingalert.common.domain.converters

import org.jetbrains.exposed.sql.ResultRow

/**
 * Interface for converting from Exposed's ResultRow to a class of the type MODEL (i.e. Website, DnsRecord, ...)
 */
interface RowConverter<MODEL> {
    fun rowToRecord(row: ResultRow): MODEL
}