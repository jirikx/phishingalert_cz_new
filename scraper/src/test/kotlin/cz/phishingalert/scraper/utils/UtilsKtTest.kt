package cz.phishingalert.scraper.utils

import cz.phishingalert.scraper.utils.checkURL
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class UtilsKtTest {

    @Test
    fun checkURLBasicTest() {
        val url = "https://www.google.com"
        assertTrue(checkURL(url))
    }

    @Test
    fun checkURLCorrectTest() {
        val url = "https://kotlinlang.org/docs/jvm-test-using-junit.html#add-dependencies"
        assertTrue(checkURL(url))
    }

    @Test
    fun checkURLInvalidTest() {
        val url = "httpsD://kotlinlang.org/docs/jvm-test-using-junit.html#add-dependencies"
        assertFalse(checkURL(url))
    }

    @Test
    fun createSubDirectory() {
    }
}