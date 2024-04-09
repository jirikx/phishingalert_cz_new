package cz.phishingalert.scraper.downloaders

import cz.phishingalert.scraper.domain.Model
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

abstract class Downloader<T> {
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    abstract fun download(url: URL): Collection<Model<T>>
}