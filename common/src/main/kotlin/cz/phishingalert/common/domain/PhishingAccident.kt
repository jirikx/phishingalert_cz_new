package cz.phishingalert.common.domain

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.UUIDColumnType
import org.jetbrains.exposed.sql.javatime.datetime
import java.net.URL
import java.time.LocalDateTime
import java.util.*

object PhishingAccidents : IntIdTable() {
    val url = varchar("url", 2000)
    val sentDate = datetime("sent_date")
    val confirmed = bool("confirmed")
    val noteText = varchar("note_text", 600).nullable()
    val sourceEmail = varchar("source_email", 300).nullable()
    val sourcePhoneNumber = varchar("source_phone_number", 100).nullable()
    val author = reference("author_id", Authors)
    val website = reference("website_id", Websites).nullable()
    val guid = uuid("guid")
}

data class PhishingAccident(
    override var id: Int?,
    var url: URL? = null,
    var sentDate: LocalDateTime,
    var confirmed: Boolean,
    var noteText: String?,
    var sourceEmail: String?,
    var sourcePhoneNumber: String?,
    var authorId: Int = 0,
    var websiteId: Int? = null,
    var guid: UUID = UUID.randomUUID()
) : Model<Int>