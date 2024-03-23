package cz.phishingalert.scraper.crawler

import java.net.URL
import java.nio.file.Path

interface Crawler {
    fun crawl(url: URL, downloadDir: Path)
}