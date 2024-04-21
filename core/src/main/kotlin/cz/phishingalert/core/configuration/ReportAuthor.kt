package cz.phishingalert.core.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "report-author")
data class ReportAuthor(
    val name: String,
    val email: String,
    val company: String?,
    val phoneNumber: String?
) {

}