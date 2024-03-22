package cz.phishingalert.scraper.downloaders

import java.net.URL

interface Downloader {
    fun download(url: URL): Unit
}