package cz.phishingalert.scraper.domain

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import java.net.URL
import java.time.LocalDate

object Websites : IntIdTable() {
    val url = varchar("url", 2000)
    val domainHolder = varchar("domain_holder", 300)
    val domainRegistrar = varchar("domain_registrar", 300)
    val country = varchar("country", 50)
    val registrationDate = date("registration")
    val lastUpdateDate = date("last_update")
    val expirationDate = date("expiration")
    val filesystemPath = varchar("filesystem-path", 300)
}

data class Website(
    override var id: Int? = null,
    var url: URL? = null,
    var domainHolder: String = "unknown",
    var domainRegistrar: String = "unknown",
    var country: String = "unknown",
    var registrationDate: LocalDate = LocalDate.EPOCH,
    var lastUpdateDate: LocalDate = LocalDate.EPOCH,
    var expirationDate: LocalDate = LocalDate.EPOCH,
    var fileSystemPath: String = "unknown"
) : Model<Int> {
//        fun isValid(): Boolean {
//            return listOf(
//                this.id, this.url, this.domainHolder, this.domainRegistrar, this.country,
//                this.registrationDate, this.lastUpdateDate, this.expirationDate,
//                this.fileSystemPath).all{it != null}
//        }
}

object WebsiteConverter : RowConverter<Website> {
    override fun rowToRecord(row: ResultRow): Website = Websites.rowToRecord(row)
}

fun Websites.rowToRecord(row: ResultRow): Website =
    Website(
        row[id].value, URL(row[url]), row[domainHolder], row[domainRegistrar], row[country], row[registrationDate],
        row[lastUpdateDate], row[expirationDate], row[filesystemPath]
        )