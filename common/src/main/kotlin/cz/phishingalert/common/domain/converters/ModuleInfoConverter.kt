package cz.phishingalert.common.domain.converters

import cz.phishingalert.common.domain.ModuleInfo
import cz.phishingalert.common.domain.ModuleInfos
import org.jetbrains.exposed.sql.ResultRow

object ModuleInfoConverter : RowConverter<ModuleInfo> {
    override fun rowToRecord(row: ResultRow): ModuleInfo = ModuleInfos.rowToRecord(row)
}

fun ModuleInfos.rowToRecord(row: ResultRow): ModuleInfo =
    ModuleInfo(
        row[id].value, row[name], row[type], row[version]
    )