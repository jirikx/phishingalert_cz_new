package cz.phishingalert.common.domain

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object PhishingAccidents : IntIdTable() {
    val sentDate = datetime("sent_date")
    val confirmed = bool("confirmed")
    val noteText = varchar("note_text", 600).nullable()
    val sourceEmail = varchar("source_email", 300).nullable()
    val sourcePhoneNumber = varchar("source_phone_number", 100).nullable()
    val author = reference("author_id", Authors)
}

data class PhishingAccident(
    override var id: Int?,
    var sentDate: LocalDateTime,
    var confirmed: Boolean,
    var noteText: String?,
    var sourceEmail: String?,
    var sourcePhoneNumber: String?,
    var authorId: Int = 0
) : Model<Int>