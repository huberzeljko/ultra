package de.peekandpoke.ultra.common.datetime

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlin.time.Duration.Companion.hours

@Suppress("unused")
class MpLocalDateSpec : StringSpec({

    "Construction" {

        MpLocalDate.of(year = 2022, month = 4, day = 1) shouldBe
                MpLocalDate.parse("2022-04-01")

        MpLocalDate.of(year = 2022, month = Month.APRIL, day = 1) shouldBe
                MpLocalDate.parse("2022-04-01")
    }

    "Genesis and Doomsday" {

        MpLocalDate.Genesis shouldBe MpLocalDate.of(-10000, Month.JANUARY, 1)

        MpLocalDate.Genesis.toIsoString() shouldBe "-10000-01-01T00:00:00.000Z"

        MpLocalDate.Doomsday shouldBe MpLocalDate.of(10000, Month.JANUARY, 1)

        MpLocalDate.Doomsday.toIsoString() shouldBe "+10000-01-01T00:00:00.000Z"
    }

    "parse - toIsoString - round trip" {
        val start = MpLocalDate.parse("2022-04-01")

        val result = MpLocalDate.parse(start.toIsoString())

        start shouldBe result
    }

    "parse - should throw on invalid input" {
        shouldThrow<IllegalArgumentException> {
            MpLocalDate.parse("")
        }
    }

    "tryParse - should return null invalid input" {
        MpLocalDate.tryParse("") shouldBe null
    }

    "Fields year, monthNumber, month, dayOfMonth, dayOfWeek, dayOfYear" {

        val subject = MpLocalDate.of(2022, Month.APRIL, 5)

        subject.year shouldBe 2022
        subject.monthNumber shouldBe 4
        subject.month shouldBe Month.APRIL
        subject.day shouldBe 5
        subject.dayOfWeek shouldBe DayOfWeek.TUESDAY
        subject.dayOfYear shouldBe 95
    }

    "Field dayOfWeek" {
        val subject = MpLocalDate.of(2022, Month.APRIL, 5)

        subject.dayOfWeek shouldBe DayOfWeek.TUESDAY
    }

    "Field dayOfYear" {
        val subject = MpLocalDate.of(2022, Month.APRIL, 5)

        subject.dayOfYear shouldBe 95
    }

    "Equality" {
        MpLocalDate.parse("2022-04-01") shouldBe MpLocalDate.parse("2022-04-01")

        MpLocalDate.parse("2022-04-01") shouldNotBe MpLocalDate.parse("2022-04-02")
    }

    "Comparison" {

        MpLocalDate.parse("2022-04-01") shouldBeEqualComparingTo
                MpLocalDate.parse("2022-04-01")

        MpLocalDate.parse("2022-04-01") shouldBeLessThan
                MpLocalDate.parse("2022-04-02")

        MpLocalDate.parse("2022-04-02") shouldBeGreaterThan
                MpLocalDate.parse("2022-04-01")
    }

    "toString" {
        MpLocalDate.parse("2022-04-02")
            .toString() shouldBe "MpLocalDate(2022-04-02T00:00:00.000Z)"
    }

    "format" {
        MpLocalDate.of(year = 2022, month = Month.APRIL, day = 11)
            .format("dd MMM yyyy") shouldBe "11 Apr 2022"

        MpLocalDate.of(year = 2022, month = Month.APRIL, day = 11)
            .formatDdMmmYyyy() shouldBe "11 Apr 2022"
    }

    "toIsoString" {
        MpLocalDate.parse("2022-04-02")
            .toIsoString() shouldBe "2022-04-02T00:00:00.000Z"
    }

    "atStartOfCentury" {

        MpLocalDate.of(2000, Month.JANUARY, 1)
            .atStartOfCentury() shouldBe MpLocalDate.of(2000, Month.JANUARY, 1)

        MpLocalDate.of(2022, Month.JANUARY, 1)
            .atStartOfCentury() shouldBe MpLocalDate.of(2000, Month.JANUARY, 1)

        MpLocalDate.of(2099, Month.DECEMBER, 31)
            .atStartOfCentury() shouldBe MpLocalDate.of(2000, Month.JANUARY, 1)

        MpLocalDate.of(2100, Month.JANUARY, 1)
            .atStartOfCentury() shouldBe MpLocalDate.of(2100, Month.JANUARY, 1)
    }

    "atStartOfDecade" {

        MpLocalDate.of(2000, Month.JANUARY, 1)
            .atStartOfDecade() shouldBe MpLocalDate.of(2000, Month.JANUARY, 1)

        MpLocalDate.of(2022, Month.APRIL, 5)
            .atStartOfDecade() shouldBe MpLocalDate.of(2020, Month.JANUARY, 1)

        MpLocalDate.of(2029, Month.DECEMBER, 31)
            .atStartOfDecade() shouldBe MpLocalDate.of(2020, Month.JANUARY, 1)

        MpLocalDate.of(2030, Month.JANUARY, 1)
            .atStartOfDecade() shouldBe MpLocalDate.of(2030, Month.JANUARY, 1)
    }

    "atStartOfYear" {
        MpLocalDate.of(2022, Month.JANUARY, 1)
            .atStartOfYear() shouldBe MpLocalDate.of(2022, Month.JANUARY, 1)

        MpLocalDate.of(2022, Month.APRIL, 5)
            .atStartOfYear() shouldBe MpLocalDate.of(2022, Month.JANUARY, 1)

        MpLocalDate.of(2022, Month.DECEMBER, 31)
            .atStartOfYear() shouldBe MpLocalDate.of(2022, Month.JANUARY, 1)
    }

    "atStartOfHalfOfYear" {

        MpLocalDate.of(2022, Month.JANUARY, 1)
            .atStartOfHalfOfYear() shouldBe MpLocalDate.of(2022, Month.JANUARY, 1)

        MpLocalDate.of(2022, Month.JUNE, 30)
            .atStartOfHalfOfYear() shouldBe MpLocalDate.of(2022, Month.JANUARY, 1)

        MpLocalDate.of(2022, Month.JULY, 1)
            .atStartOfHalfOfYear() shouldBe MpLocalDate.of(2022, Month.JULY, 1)

        MpLocalDate.of(2022, Month.DECEMBER, 31)
            .atStartOfHalfOfYear() shouldBe MpLocalDate.of(2022, Month.JULY, 1)
    }

    "atStartOfQuarterOfYear" {

        MpLocalDate.of(2022, Month.JANUARY, 1)
            .atStartOfQuarterOfYear() shouldBe MpLocalDate.of(2022, Month.JANUARY, 1)

        MpLocalDate.of(2022, Month.MARCH, 31)
            .atStartOfQuarterOfYear() shouldBe MpLocalDate.of(2022, Month.JANUARY, 1)

        MpLocalDate.of(2022, Month.APRIL, 1)
            .atStartOfQuarterOfYear() shouldBe MpLocalDate.of(2022, Month.APRIL, 1)

        MpLocalDate.of(2022, Month.JUNE, 30)
            .atStartOfQuarterOfYear() shouldBe MpLocalDate.of(2022, Month.APRIL, 1)

        MpLocalDate.of(2022, Month.JULY, 1)
            .atStartOfQuarterOfYear() shouldBe MpLocalDate.of(2022, Month.JULY, 1)

        MpLocalDate.of(2022, Month.SEPTEMBER, 30)
            .atStartOfQuarterOfYear() shouldBe MpLocalDate.of(2022, Month.JULY, 1)

        MpLocalDate.of(2022, Month.OCTOBER, 1)
            .atStartOfQuarterOfYear() shouldBe MpLocalDate.of(2022, Month.OCTOBER, 1)

        MpLocalDate.of(2022, Month.DECEMBER, 31)
            .atStartOfQuarterOfYear() shouldBe MpLocalDate.of(2022, Month.OCTOBER, 1)
    }

    "atStartOfMonth" {
        MpLocalDate.of(2022, Month.JANUARY, 1)
            .atStartOfMonth() shouldBe MpLocalDate.of(2022, Month.JANUARY, 1)

        MpLocalDate.of(2022, Month.APRIL, 5)
            .atStartOfMonth() shouldBe MpLocalDate.of(2022, Month.APRIL, 1)

        MpLocalDate.of(2022, Month.DECEMBER, 31)
            .atStartOfMonth() shouldBe MpLocalDate.of(2022, Month.DECEMBER, 1)
    }

    "atStartOfNext(dayOfWeek)" {

        MpLocalDate.parse("2022-04-04")
            .atStartOfNext(DayOfWeek.TUESDAY).let {
                it shouldBe MpLocalDate.parse("2022-04-05")
            }

        MpLocalDate.parse("2022-04-05")
            .atStartOfNext(DayOfWeek.TUESDAY).let {
                it shouldBe MpLocalDate.parse("2022-04-12")
            }
    }

    "atStartOfPrevious(dayOfWeek)" {

        MpLocalDate.parse("2022-04-04")
            .atStartOfPrevious(DayOfWeek.TUESDAY).let {
                it shouldBe MpLocalDate.parse("2022-03-29")
            }

        MpLocalDate.parse("2022-04-05")
            .atStartOfPrevious(DayOfWeek.TUESDAY).let {
                it shouldBe MpLocalDate.parse("2022-04-05")
            }
    }

    "atStartOfDay" {

        val subject = MpLocalDate.of(2022, Month.APRIL, 5)

        subject.atStartOfDay(TimeZone.UTC) shouldBe
                MpZonedDateTime.parse("2022-04-05T00:00:00.000Z")

        subject.atStartOfDay(TimeZone.of("Europe/Bucharest")) shouldBe
                MpZonedDateTime.parse("2022-04-05T00:00:00.000[Europe/Bucharest]")
    }

    "atStartOfDay - Bucharest" {

        val timezone = TimeZone.of("Europe/Bucharest")

        val date = MpLocalDate.of(2022, Month.APRIL, 5)

        val startOfDay = date.atStartOfDay(timezone)

        withClue("Bucharest is 3 hours ahead of UTC") {
            startOfDay.toIsoString() shouldBe "2022-04-05T00:00:00.000[Europe/Bucharest]"
        }
    }

    MpTimezone.supportedIds.forEach { timezoneId ->

        "atStartOfDay - at timezone '$timezoneId'" {

            val date = MpLocalDate.of(2022, Month.APRIL, 5)
            val instant = date.atStartOfDay(TimeZone.UTC).toInstant()

            val timezone = MpTimezone.of(timezoneId)

            val startOfDay = date.atStartOfDay(timezone)

            val startOfDayTs = startOfDay.toEpochSeconds()

            val offset = timezone.offsetAt(instant).totalSeconds
            val expectedTs = instant.toEpochSeconds() - offset

            startOfDayTs shouldBe expectedTs
        }
    }

    "atTime(time)" {
        val subject = MpLocalDate.of(
            year = 2022,
            month = Month.APRIL,
            day = 5
        ).atTime(
            MpLocalTime.of(
                hour = 12,
                minute = 13,
                second = 14,
                milliSecond = 123,
            )
        )

        subject shouldBe MpLocalDateTime.of(
            year = 2022,
            month = Month.APRIL,
            day = 5,
            hour = 12,
            minute = 13,
            second = 14,
            milliSecond = 123,
        )
    }

    "atTime(time, timezone)" {

        // Not Crossing DST in Berlin
        val notCrossedDst = MpLocalDate.of(2022, 3, 27)
            .atTime(MpLocalTime.of(2, 0), MpTimezone.of("Europe/Berlin"))

        // Crossing DST in Berlin
        val crossedDst = MpLocalDate.of(2022, 3, 27)
            .atTime(MpLocalTime.of(4, 0), MpTimezone.of("Europe/Berlin"))

        notCrossedDst shouldBe MpZonedDateTime.parse("2022-03-27T02:00:00[Europe/Berlin]")

        crossedDst shouldBe MpZonedDateTime.parse("2022-03-27T04:00:00[Europe/Berlin]")

        (crossedDst - notCrossedDst) shouldBe 1.hours
    }

    "toLocalDateTime" {

        val subject = MpLocalDate.of(2022, Month.APRIL, 5)

        subject.toLocalDateTime() shouldBe
                MpLocalDateTime.of(2022, Month.APRIL, 5)
    }

    "toRange(timeslot, timezone)" {

        val date = MpLocalDate.of(2022, Month.APRIL, 5)
        val slot = MpLocalTimeSlot.of(MpLocalTime.of(12, 0), 1.hours)

        val range = date.toRange(slot, MpTimezone.of("Europe/Berlin"))

        range.from shouldBe MpZonedDateTime.parse("2022-04-05T12:00:00.000[Europe/Berlin]")
        range.to shouldBe MpZonedDateTime.parse("2022-04-05T13:00:00.000[Europe/Berlin]")
    }

    "Arithmetic - plus days (DateTimeUnit.DAY)" {

        val start = MpLocalDate.of(2022, Month.APRIL, 5)

        start.plus(DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 6)
        start.plus(2, DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 7)
        start.plus(2L, DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 7)
        start.plus(-2, DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 3)
        start.plus(-2L, DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 3)
    }

    "Arithmetic - minus days (DateTimeUnit.DAY)" {

        val start = MpLocalDate.of(2022, Month.APRIL, 5)

        start.minus(DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 4)
        start.minus(2, DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 3)
        start.minus(2L, DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 3)
        start.minus(-2, DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 7)
        start.minus(-2L, DateTimeUnit.DAY) shouldBe MpLocalDate.of(2022, Month.APRIL, 7)
    }

    "Arithmetic - plus months (DateTimeUnit.MONTH)" {

        val start = MpLocalDate.of(2022, Month.APRIL, 5)

        start.plus(DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2022, Month.MAY, 5)
        start.plus(5, DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2022, Month.SEPTEMBER, 5)
        start.plus(5L, DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2022, Month.SEPTEMBER, 5)
        start.plus(-5, DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2021, Month.NOVEMBER, 5)
        start.plus(-5L, DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2021, Month.NOVEMBER, 5)
    }

    "Arithmetic - minus months (DateTimeUnit.MONTH)" {

        val start = MpLocalDate.of(2022, Month.APRIL, 5)

        start.minus(DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2022, Month.MARCH, 5)
        start.minus(5, DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2021, Month.NOVEMBER, 5)
        start.minus(5L, DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2021, Month.NOVEMBER, 5)
        start.minus(-5, DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2022, Month.SEPTEMBER, 5)
        start.minus(-5L, DateTimeUnit.MONTH) shouldBe MpLocalDate.of(2022, Month.SEPTEMBER, 5)
    }

    "Arithmetic - plus years (DateTimeUnit.YEAR)" {

        val start = MpLocalDate.of(2022, Month.APRIL, 5)

        start.plus(DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2023, Month.APRIL, 5)
        start.plus(5, DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2027, Month.APRIL, 5)
        start.plus(5L, DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2027, Month.APRIL, 5)
        start.plus(-5, DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2017, Month.APRIL, 5)
        start.plus(-5L, DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2017, Month.APRIL, 5)
    }

    "Arithmetic - minus years (DateTimeUnit.YEAR)" {

        val start = MpLocalDate.of(2022, Month.APRIL, 5)

        start.minus(DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2021, Month.APRIL, 5)
        start.minus(5, DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2017, Month.APRIL, 5)
        start.minus(5L, DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2017, Month.APRIL, 5)
        start.minus(-5, DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2027, Month.APRIL, 5)
        start.minus(-5L, DateTimeUnit.YEAR) shouldBe MpLocalDate.of(2027, Month.APRIL, 5)
    }

    "plusDays - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).plusDays(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusDays(1) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 1)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusDays(2) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 2)

        MpLocalDate.of(2020, Month.MARCH, 1).plusDays(-1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 29)

        MpLocalDate.of(2020, Month.MARCH, 1).plusDays(-2) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 28)
    }

    "minusDays - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).minusDays(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusDays(-1) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 1)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusDays(-2) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 2)

        MpLocalDate.of(2020, Month.MARCH, 1).minusDays(1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 29)

        MpLocalDate.of(2020, Month.MARCH, 1).minusDays(2) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 28)
    }

    "plusWeeks - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).plusWeeks(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusWeeks(1) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 7)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusWeeks(2) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 14)

        MpLocalDate.of(2020, Month.MARCH, 7).plusWeeks(-1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 29)

        MpLocalDate.of(2020, Month.MARCH, 7).plusWeeks(-2) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 22)
    }

    "minusWeeks - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).minusWeeks(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusWeeks(-1) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 7)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusWeeks(-2) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 14)

        MpLocalDate.of(2020, Month.MARCH, 7).minusWeeks(1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 29)

        MpLocalDate.of(2020, Month.MARCH, 7).minusWeeks(2) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 22)
    }

    "plusMonths - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).plusMonths(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusMonths(1) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 29)

        MpLocalDate.of(2020, Month.JANUARY, 31).plusMonths(2) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 31)

        MpLocalDate.of(2020, Month.MARCH, 7).plusMonths(-1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 7)

        MpLocalDate.of(2020, Month.MARCH, 31).plusMonths(-1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 29)

        MpLocalDate.of(2020, Month.MAY, 31).plusMonths(-1) shouldBe
                MpLocalDate.of(2020, Month.APRIL, 30)

        MpLocalDate.of(2020, Month.MAY, 30).plusMonths(-1) shouldBe
                MpLocalDate.of(2020, Month.APRIL, 30)

        MpLocalDate.of(2020, Month.MAY, 29).plusMonths(-1) shouldBe
                MpLocalDate.of(2020, Month.APRIL, 29)
    }

    "minusMonths - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).minusMonths(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusMonths(-1) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 29)

        MpLocalDate.of(2020, Month.JANUARY, 31).minusMonths(-2) shouldBe
                MpLocalDate.of(2020, Month.MARCH, 31)

        MpLocalDate.of(2020, Month.MARCH, 7).minusMonths(1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 7)

        MpLocalDate.of(2020, Month.MARCH, 31).minusMonths(1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 29)

        MpLocalDate.of(2020, Month.MAY, 31).minusMonths(1) shouldBe
                MpLocalDate.of(2020, Month.APRIL, 30)

        MpLocalDate.of(2020, Month.MAY, 30).minusMonths(1) shouldBe
                MpLocalDate.of(2020, Month.APRIL, 30)

        MpLocalDate.of(2020, Month.MAY, 29).minusMonths(1) shouldBe
                MpLocalDate.of(2020, Month.APRIL, 29)
    }

    "plusYears - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).plusYears(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusYears(1) shouldBe
                MpLocalDate.of(2021, Month.FEBRUARY, 28)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusYears(2) shouldBe
                MpLocalDate.of(2022, Month.FEBRUARY, 28)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusYears(4) shouldBe
                MpLocalDate.of(2024, Month.FEBRUARY, 29)

        MpLocalDate.of(2021, Month.FEBRUARY, 28).plusYears(-1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 28)

        MpLocalDate.of(2022, Month.FEBRUARY, 28).plusYears(-2) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 28)

        MpLocalDate.of(2024, Month.FEBRUARY, 29).plusYears(-4) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 29)
    }

    "minusYears - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).minusYears(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusYears(-1) shouldBe
                MpLocalDate.of(2021, Month.FEBRUARY, 28)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusYears(-2) shouldBe
                MpLocalDate.of(2022, Month.FEBRUARY, 28)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusYears(-4) shouldBe
                MpLocalDate.of(2024, Month.FEBRUARY, 29)

        MpLocalDate.of(2021, Month.FEBRUARY, 28).minusYears(1) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 28)

        MpLocalDate.of(2022, Month.FEBRUARY, 28).minusYears(2) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 28)

        MpLocalDate.of(2024, Month.FEBRUARY, 29).minusYears(4) shouldBe
                MpLocalDate.of(2020, Month.FEBRUARY, 29)
    }

    "plusCenturies - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).plusCenturies(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusCenturies(1) shouldBe
                MpLocalDate.of(2120, Month.FEBRUARY, 29)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).plusCenturies(2) shouldBe
                MpLocalDate.of(2220, Month.FEBRUARY, 29)

        MpLocalDate.of(2000, Month.FEBRUARY, 29).plusCenturies(1) shouldBe
                MpLocalDate.of(2100, Month.FEBRUARY, 28)

        MpLocalDate.of(2000, Month.FEBRUARY, 29).plusCenturies(-1) shouldBe
                MpLocalDate.of(1900, Month.FEBRUARY, 28)

        MpLocalDate.of(2000, Month.FEBRUARY, 29).plusCenturies(-4) shouldBe
                MpLocalDate.of(1600, Month.FEBRUARY, 29)
    }

    "minusCenturies - edge cases" {

        MpLocalDate.of(2022, Month.APRIL, 5).minusCenturies(0) shouldBe
                MpLocalDate.of(2022, Month.APRIL, 5)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusCenturies(-1) shouldBe
                MpLocalDate.of(2120, Month.FEBRUARY, 29)

        MpLocalDate.of(2020, Month.FEBRUARY, 29).minusCenturies(-2) shouldBe
                MpLocalDate.of(2220, Month.FEBRUARY, 29)

        MpLocalDate.of(2000, Month.FEBRUARY, 29).minusCenturies(-1) shouldBe
                MpLocalDate.of(2100, Month.FEBRUARY, 28)

        MpLocalDate.of(2000, Month.FEBRUARY, 29).minusCenturies(1) shouldBe
                MpLocalDate.of(1900, Month.FEBRUARY, 28)

        MpLocalDate.of(2000, Month.FEBRUARY, 29).minusCenturies(4) shouldBe
                MpLocalDate.of(1600, Month.FEBRUARY, 29)
    }

    "plus(period: MpTemporalPeriod)" {
        MpLocalDate.of(2022, Month.APRIL, 5)
            .plus(MpDatePeriod(years = 1, months = 2, days = 3)) shouldBe
                MpLocalDate.of(2023, Month.JUNE, 8)
    }

    "minus(period: MpTemporalPeriod)" {
        MpLocalDate.of(2022, Month.APRIL, 5)
            .minus(MpDatePeriod(years = 1, months = 2, days = 3)) shouldBe
                MpLocalDate.of(2021, Month.FEBRUARY, 2)
    }
})
