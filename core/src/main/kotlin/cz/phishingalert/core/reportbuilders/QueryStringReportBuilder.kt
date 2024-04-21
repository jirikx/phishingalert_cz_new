package cz.phishingalert.core.reportbuilders

/**
 * Build a query string which can be later used in a URL
 */
class QueryStringReportBuilder : ReportBuilder {
    private val map: MutableMap<String, MutableList<String>> = mutableMapOf()

    /**
     * Add (key, value) pair to the builder
     * One key can have multiple values
     */
    fun addParameter(key: String, value: String) {
        if (map.contains(key))
            map[key]?.add(value)
        else
            map[key] = mutableListOf(value)
    }

    /**
     * Generate query from the builder
     * Keys with multiple values use [valueSeparator] between the values
     * Example result: ?name=petr&skills=cleaning%0Adrinking
     */
    fun getQuery(valueSeparator: String = "%0A"): String {
        val builder = StringBuilder("?")
        map.forEach { (key, value) ->
            builder.append("$key=")
            if (value.size == 1)
                builder.append(value.first())
            else
                value.forEach { builder.append("$it$valueSeparator") }
            builder.append("&")
        }

        return builder.toString()
    }
}

