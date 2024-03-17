package cz.phishingalert.scraper.domain

import java.net.URL
import java.util.Date

data class Website(
    override var id: Int?,
    var url: URL?,
    var domainHolder: String?,
    var domainRegistrar: String?,
    var country: String?,
    var registrationDate: Date?,
    var lastUpdateDate: Date?,
    var expirationDate: Date?,
    var fileSystemPath: String?
) : Entity<Int> {
//        fun isValid(): Boolean {
//            return listOf(
//                this.id, this.url, this.domainHolder, this.domainRegistrar, this.country,
//                this.registrationDate, this.lastUpdateDate, this.expirationDate,
//                this.fileSystemPath).all{it != null}
//        }
}