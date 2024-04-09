package cz.phishingalert.scraper.domain

import org.jetbrains.exposed.sql.ResultRow

/**
 * Interface for implementing objects that can be converted from Exposed's ResultRow to a class of the type T
 */
interface RowConverter<T> {
    fun rowToRecord(row: ResultRow): T
}