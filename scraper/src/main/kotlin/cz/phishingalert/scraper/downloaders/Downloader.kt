package cz.phishingalert.scraper.downloaders

interface Downloader {
    fun download(domain: String): Unit
}