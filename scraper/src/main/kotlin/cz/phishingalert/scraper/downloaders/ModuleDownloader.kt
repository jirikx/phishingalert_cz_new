package cz.phishingalert.scraper.downloaders

import com.microsoft.playwright.Playwright
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.io.FileInputStream

@Component
class ModuleDownloader : Downloader {
    override fun download(domain: String) {
        // https://github.com/johnmichel/Library-Detector-for-Chrome/blob/master/library/libraries.js
        val playwright = Playwright.create()
        val browser = playwright.firefox().launch()

        val libraries = loadResources("classpath:libraries.js")

        val libraryName = Regex("'([^']+?)': \\{")
        val matches = libraryName.findAll(libraries)

        val page = browser.newPage()
        page.navigate("https://${domain}")
        page.evaluate(libraries)

        for (match in matches) {
            val module = match.groupValues[1]
            val testResult = page.evaluate("d41d8cd98f00b204e9800998ecf8427e_LibraryDetectorTests['$module'].test(window)")
            if (testResult.toString().trimIndent() != "false")
                println("Found $module with version $testResult")
        }

        // check Angular: https://github.com/rangle/augury/blob/master/src/backend/utils/app-check.ts
    }

    fun loadResources(path: String): String {
        val file = ResourceUtils.getFile(path)
        val inputStream = FileInputStream(file)

        return inputStream.readAllBytes().toString(Charsets.UTF_8)
    }
}