package cz.phishingalert.scraper.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class DateParserTest {

    @Test
    fun parseVariousFormats() {
        assertEquals(DateParser.parse("2025-06-22"), LocalDate.of(2025, 6, 22))
        assertEquals(DateParser.parse("25.03.2003 14:16:00"), LocalDate.of(2003, 3, 25))
        assertEquals(DateParser.parse("2025-02-26T10:39:41.00Z"), LocalDate.of(2025, 2, 26))
        assertEquals(DateParser.parse("2021/02/26 10:39:41"), LocalDate.of(2021, 2, 26))
        assertEquals(DateParser.parse("2021/02/26"), LocalDate.of(2021, 2, 26))
    }
}