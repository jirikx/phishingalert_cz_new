package cz.phishingalert.scraper.downloaders

import com.microsoft.playwright.Playwright
import cz.phishingalert.common.domain.ModuleInfo
import cz.phishingalert.common.domain.ModuleType
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.io.FileInputStream
import java.net.URL

@Component
class ModuleDownloader(val playwright: Playwright) : Downloader<ModuleInfo>() {
    override fun download(url: URL): List<ModuleInfo> {
        // https://github.com/johnmichel/Library-Detector-for-Chrome/blob/master/library/libraries.js
        val browser = playwright.firefox().launch()

        val libraries = loadResources("classpath:libraries.js")

        val libraryName = Regex("'([^']+?)': \\{")
        val matches = libraryName.findAll(libraries)

        val page = browser.newPage()
        page.navigate(url.toString())
        page.evaluate(libraries)

        val results = mutableListOf<ModuleInfo>()
        for (match in matches) {
            val module = match.groupValues[1]
            val testResult = page
                .evaluate("d41d8cd98f00b204e9800998ecf8427e_LibraryDetectorTests['$module'].test(window)")
                .toString().trimIndent()
            if (testResult != "false") {
                results.add(ModuleInfo(
                    0,
                    module,
                    ModuleType.LIBRARY,
                    extractVersion(testResult)
                ))
                logger.info("Found $module with version ${extractVersion(testResult)}")
            }
        }

        //todo https://github.com/enthec/webappanalyzer
        // https://github.com/micmro/Stylify-Me

        page.close()
        return results.toList()
        // check Angular: https://github.com/rangle/augury/blob/master/src/backend/utils/app-check.ts
    }

    fun loadResources(path: String): String {
        val file = ResourceUtils.getFile(path)
        val inputStream = FileInputStream(file)

        return inputStream.readAllBytes().toString(Charsets.UTF_8)
    }

    /**
     * Extract the version value from the defined format
     * Examples of the format: {version=1.2.5}, {version=0.8}, {version=null}
     */
    fun extractVersion(rawVersion: String): String {
        val pattern = Regex("""version=([\w.]+)""")
        val result = pattern.find(rawVersion)

        if (result != null) {
            val version = result.groups[1]?.value ?: return "error"
            if (version == "null") return "unknown"
            return version
        } else
            return "error"
    }
}