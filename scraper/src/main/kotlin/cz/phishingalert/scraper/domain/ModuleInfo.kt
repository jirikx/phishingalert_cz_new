package cz.phishingalert.scraper.domain

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object ModuleInfos : IntIdTable() {
    val name = varchar("name", 100)
    val type = enumeration("type", ModuleType::class) //ModuleType?,
    val version = varchar("version", 30)
}

enum class ModuleType(val code: String) {
    LIBRARY("Library"), FRAMEWORK("Framework"), OTHER("Other")
}

data class ModuleInfo(
    override var id: Int?,
    var name: String,
    var type: ModuleType? = ModuleType.LIBRARY,
    var version: String,
) : Model<Int>

object ModuleInfoConverter : RowConverter<ModuleInfo> {
    override fun rowToRecord(row: ResultRow): ModuleInfo  = ModuleInfos.rowToRecord(row)
}

fun ModuleInfos.rowToRecord(row: ResultRow): ModuleInfo =
    ModuleInfo(
        row[id].value, row[name], row[type], row[version]
    )