package cz.phishingalert.scraper.repository.generic

import cz.phishingalert.scraper.domain.Model
import cz.phishingalert.scraper.domain.RowConverter
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*

/**
 * Repository for tables which use integer as an ID for their rows
 */
abstract class IntTableRepository<MODEL : Model<Int>, TABLE : IntIdTable> (
    val table: TABLE,
    val converter: RowConverter<MODEL>
) : CrudRepository<MODEL, Int> {
    override fun find(id: Int): MODEL? {
        val row = table.selectAll().where { table.id eq id }.singleOrNull()
        return if (row != null)
            converter.rowToRecord(row)
        else
            null
    }

    override fun findAll(): Collection<MODEL> {
        return table.selectAll().map { converter.rowToRecord(it) }
    }

    override fun delete(id: Int): Boolean {
        val deleted = table.deleteWhere { table.id eq id }
        return deleted != 0
    }
}
