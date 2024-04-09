package cz.phishingalert.scraper.domain

import org.jetbrains.exposed.sql.ResultRow

interface Model<ID> {
    var id: ID?
}