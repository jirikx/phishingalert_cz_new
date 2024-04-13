package cz.phishingalert.common.domain

import org.jetbrains.exposed.dao.id.IntIdTable

object Authors : IntIdTable() {
    val name = varchar("name", 300)
    val email = varchar("email", 300)
    val userAgent = varchar("user_agent", 300)
    val ipAddress = varchar("ip_address", 100)
}

data class Author(
    override var id: Int?,
    var name: String,
    var email: String,
    var userAgent: String,
    var ipAddress: String
) : Model<Int>