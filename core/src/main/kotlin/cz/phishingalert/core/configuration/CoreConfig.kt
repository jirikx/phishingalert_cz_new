package cz.phishingalert.core.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "settings")
data class CoreConfig(
    val reportAuthor: ReportAuthor,
    val sftpDirectory: String,
    val minTimeDiff: Int,
    val requireEmailConfirmation: Boolean,
    val googleCloudProjectId: String,
    val googleCloudConnectionTimeout: Long
) {
    /**
     * This class stores info about the person who is responsible for the reporting (usually the project admin)
     */
    @ConfigurationProperties(prefix = "settings.report-author")
    data class ReportAuthor(
        val name: String,
        val email: String,
        val company: String?,
        val phoneNumber: String?
    )
}
