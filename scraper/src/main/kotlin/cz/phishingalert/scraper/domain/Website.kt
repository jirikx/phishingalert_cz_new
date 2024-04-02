package cz.phishingalert.scraper.domain

import java.net.URL
import java.time.LocalDate

data class Website(
    override var id: Int? = null,
    var url: URL? = null,
    var domainHolder: String? = null,
    var domainRegistrar: String? = null,
    var country: String? = "unknown",
    var registrationDate: LocalDate? = null,
    var lastUpdateDate: LocalDate? = null,
    var expirationDate: LocalDate? = null,
    var fileSystemPath: String? = null
) : Entity<Int> {
//        fun isValid(): Boolean {
//            return listOf(
//                this.id, this.url, this.domainHolder, this.domainRegistrar, this.country,
//                this.registrationDate, this.lastUpdateDate, this.expirationDate,
//                this.fileSystemPath).all{it != null}
//        }
}