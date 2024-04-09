package cz.phishingalert.scraper.domain

import org.jetbrains.exposed.sql.Table

/**
 * Table for M:N relation between Website and ModuleInfo
 */
object WebsiteModuleInfos : Table() {
    val websiteId = reference("website_id", Websites)
    val moduleInfoId = reference("module_info_id", ModuleInfos)

    override val primaryKey = PrimaryKey(websiteId, moduleInfoId)
}