package cz.phishingalert.common.domain

import cz.phishingalert.common.domain.converters.RowConverter
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.net.URL
import java.time.LocalDate

object Websites : IntIdTable() {
    val domainHolder = varchar("domain_holder", 300)
    val domainRegistrar = varchar("domain_registrar", 300)
    val country = varchar("country", 50)
    val registrationDate = date("registration")
    val lastUpdateDate = date("last_update")
    val expirationDate = date("expiration")
    val filesystemPath = varchar("filesystem-path", 300)
    val phishingAccident = reference("phishing_accident_id", PhishingAccidents).nullable()
}

data class Website(
    override var id: Int? = null,
    var domainHolder: String = "unknown",
    var domainRegistrar: String = "unknown",
    var country: String = "unknown",
    var registrationDate: LocalDate = LocalDate.EPOCH,
    var lastUpdateDate: LocalDate = LocalDate.EPOCH,
    var expirationDate: LocalDate = LocalDate.EPOCH,
    var fileSystemPath: String = "unknown",
    var phishingAccident: PhishingAccident? = null
) : Model<Int>