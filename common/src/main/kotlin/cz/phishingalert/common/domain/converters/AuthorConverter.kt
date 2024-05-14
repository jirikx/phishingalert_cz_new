package cz.phishingalert.common.domain.converters

import cz.phishingalert.common.domain.Author
import cz.phishingalert.common.domain.Authors
import org.jetbrains.exposed.sql.ResultRow

object AuthorConverter : RowConverter<Author> {
    override fun rowToRecord(row: ResultRow): Author = Authors.rowToRecord(row)
}

fun Authors.rowToRecord(row: ResultRow): Author =
    Author(row[id].value, row[name], row[email], row[userAgent], row[ipAddress])