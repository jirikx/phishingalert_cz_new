package cz.phishingalert.scraper.utils
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

object DateParser {
    private val formatter = DateTimeFormatter.ofPattern(
        "[yyyy-MM-dd]" +
                "[dd-MM-yyyy]" +
                "[yyyy/MM/dd]" +
                "[dd/MM/yyyy]" +
                "[yyyy.MM.dd]" +
                "[dd.MM.yyyy]"
    )
    private val datePattern = Regex("(\\d{2,4}.\\d{2}.\\d{2,4})")

    /**
     * Try to parse the given date string
     * @return the date of Unix epoch if the parsing failed
     */
    fun parse(rawDate: String?): LocalDate {
        return try {
            val pureDate = rawDate?.let { datePattern.find(it) } ?: throw DateTimeException("Wrong format")
            LocalDate.parse(pureDate.value, formatter)
        } catch (ex: DateTimeException) {
            LocalDate.of(1970, 1, 1)   // 00:00:00 1 January 1970
        }
    }
}