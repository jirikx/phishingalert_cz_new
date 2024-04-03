package cz.phishingalert.scraper.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import java.net.URL

class UtilsKtTest {

    @Test
    fun checkURLBasicTest() {
        val url = "https://www.google.com"
        assertTrue(checkURL(url, true))
    }

    @Test
    fun checkURLCorrectTest() {
        val url = "https://kotlinlang.org/docs/jvm-test-using-junit.html#add-dependencies"
        assertTrue(checkURL(url, true))
    }

    @Test
    fun checkURLInvalidTest() {
        val url = "httpsD://kotlinlang.org/docs/jvm-test-using-junit.html#add-dependencies"
        assertFalse(checkURL(url))
    }

    @Test
    fun toRootDomainCorrectTest() {
        val url = URL("https://www.fit.cvut.cz/cs/studium/pruvodce-studiem/bakalarske-a-magisterske-studium").host
        assertEquals("cvut.cz", toRootDomain(url))
    }

    @Test
    fun toRootDomainInvalidTest() {
        assertThrows<IllegalStateException> {
            val url = "www_dw_com"
            toRootDomain(url)
        }
    }

    @Test
    fun toHostWithoutWwwCorrectTest() {
        val url = URL("https://www.fit.cvut.cz/cs/studium/pruvodce-studiem/bakalarske-a-magisterske-studium")
        val expected = "fit.cvut.cz"
        val actual = toHostWithoutWww(url)
        assertEquals(expected, actual)
    }

    @Test
    fun createSubDirectory() {
    }
}