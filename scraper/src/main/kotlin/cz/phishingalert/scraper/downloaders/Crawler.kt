package cz.phishingalert.scraper.downloaders

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL
import java.io.File
import java.util.*


class Crawler(val storageFolder: File) : WebCrawler() {
    override fun shouldVisit(referringPage: Page?, url: WebURL?): Boolean {
        val href = url?.url?.lowercase(Locale.getDefault()) ?: return false
        //return href.startsWith("https://learning-js.pages.dev")
        return true
    }

    override fun shouldFollowLinksIn(url: WebURL?): Boolean {
        return super.shouldFollowLinksIn(url)
    }

    override fun visit(page: Page?) {
        val url = page?.webURL?.url ?: return
        println("URL: $url")

        if (page.parseData is HtmlParseData) {
            val htmlParseData = page.parseData as HtmlParseData
            val text = htmlParseData.text
            val html = htmlParseData.html
            val links = htmlParseData.outgoingUrls

            println("Text length: " + text.length)
            println("Html length: " + html.length)
            println("Number of outgoing links: " + links.size)
        }
    }
}