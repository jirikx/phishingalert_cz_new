package cz.phishingalert.scraper

/**
 * Check if the given string is a valid URL
 * Source of the regex: https://stackoverflow.com/a/26987741
 */
fun checkURL(url: String): Boolean {
    val regex = Regex("^(((?!-))(xn--|_)?[a-z0-9-]{0,61}[a-z0-9]{1,1}\\.)*(xn--)?([a-z0-9][a-z0-9\\-]{0,60}|[a-z0-9-]{1,30}\\.[a-z]{2,})\$")
    return regex.matches(url)
}