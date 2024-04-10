package cz.phishingalert.scraper.domain

import org.jetbrains.exposed.dao.id.IntIdTable

object ModuleInfos : IntIdTable() {
    val name = varchar("name", 100)
    val type = enumeration("type", ModuleType::class) //ModuleType?,
    val version = varchar("version", 30)

    init {
        uniqueIndex(name, type, version)
    }
}

enum class ModuleType(val code: String) {
    LIBRARY("Library"), FRAMEWORK("Framework"), OTHER("Other")
}

data class ModuleInfo(
    override var id: Int?,
    var name: String,
    var type: ModuleType = ModuleType.LIBRARY,
    var version: String,
) : Model<Int>