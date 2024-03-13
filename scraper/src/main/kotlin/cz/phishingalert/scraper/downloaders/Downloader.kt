package cz.phishingalert.scraper.downloaders

interface Downloader {
    fun download(): Unit
}