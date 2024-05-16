package cz.phishingalert.scraper.utils

import com.google.common.net.InternetDomainName
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * Check if the given string is a valid URL
 * Source of the regex: https://stackoverflow.com/a/26987741
 */
fun checkURL(url: String, checkProtocol: Boolean = false): Boolean {
    val regex = Regex("^(((?!-))(xn--|_)?[a-z0-9-]{0,61}[a-z0-9]{1,1}\\.)*(xn--)?([a-z0-9][a-z0-9\\-]{0,60}|[a-z0-9-]{1,30}\\.[a-z]{2,})\$")

    return if (checkProtocol)
        try {
            URL(url).toURI()
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    else
        regex.matches(url)
}

fun toRootDomain(url: String): String {
    // This is a hack which is needed because Guava handles badly .dev subdomains
    if (url.endsWith(".dev")) {
        val parts = url.split(".")
        if (parts.size >= 2) {
            return "${parts[parts.size - 2]}.${parts[parts.size - 1]}"
        }
    }

    return InternetDomainName.from(url).topPrivateDomain().toString()
}

fun toHostWithoutWww(url: URL): String {
    val host = url.host
    if (host.startsWith("www."))
        return host.removePrefix("www.")
    return host
}

/**
 * Check if the subdirectory in the given dir exists and if it doesn't, then create it
 */
fun createSubDirectory(dir: Path, subDirName: String): Path {
    val subDir = dir.resolve(subDirName)
    if (!subDir.exists())
        Files.createDirectory(subDir)

    return subDir
}