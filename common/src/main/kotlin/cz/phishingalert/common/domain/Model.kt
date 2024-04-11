package cz.phishingalert.common.domain

import org.jetbrains.exposed.sql.ResultRow

interface Model<ID> {
    var id: ID?
}