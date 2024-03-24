package cz.phishingalert.scraper.domain

enum class ModuleType(val code: String) {
    LIBRARY("Library"), FRAMEWORK("Framework"), OTHER("Other")
}

data class ModuleInfo(
    override var id: Int?,
    var name: String,
    var type: String,
    var version: ModuleType?
) : Entity<Int> {
}