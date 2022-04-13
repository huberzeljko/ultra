package de.peekandpoke.ultra.common.datetime

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@Suppress("unused")
class MpLocalTimeSpec : StringSpec({

    "parse" {
        MpLocalTime.parse("12:00") shouldBe
                MpLocalTime.of(hour = 12, minute = 0)

        MpLocalTime.parse("12:13:14") shouldBe
                MpLocalTime.of(hour = 12, minute = 13, second = 14)

        MpLocalTime.parse("12:13:14.123") shouldBe
                MpLocalTime.of(hour = 12, minute = 13, second = 14, milliSecond = 123)
    }

    "Creation - ofMilliSeconds()" {

        MpLocalTime.ofMilliSeconds(
            12 * MpLocalTime.MillisPerHour +
                    13 * MpLocalTime.MillisPerMinute +
                    14 * MpLocalTime.MillisPerSecond +
                    123
        ) shouldBe MpLocalTime.of(hour = 12, minute = 13, second = 14, milliSecond = 123)

        MpLocalTime.ofMilliSeconds(
            36 * MpLocalTime.MillisPerHour +
                    13 * MpLocalTime.MillisPerMinute +
                    14 * MpLocalTime.MillisPerSecond +
                    123
        ) shouldBe MpLocalTime.of(hour = 12, minute = 13, second = 14, milliSecond = 123)
    }

    "Creation - ofSecondOfDay()" {

        MpLocalTime.ofSecondOfDay(
            (12 * MpLocalTime.MillisPerHour +
                    13 * MpLocalTime.MillisPerMinute +
                    14 * MpLocalTime.MillisPerSecond +
                    123) / 1_000
        ) shouldBe MpLocalTime.of(hour = 12, minute = 13, second = 14, milliSecond = 0)

        MpLocalTime.ofSecondOfDay(
            (36 * MpLocalTime.MillisPerHour +
                    13 * MpLocalTime.MillisPerMinute +
                    14 * MpLocalTime.MillisPerSecond +
                    123) / 1_000
        ) shouldBe MpLocalTime.of(hour = 12, minute = 13, second = 14, milliSecond = 0)
    }

    "Creation - Min, Max, Noon" {

        MpLocalTime.Min.toIsoString() shouldBe "00:00:00.000"

        MpLocalTime.Max.toIsoString() shouldBe "23:59:59.999"

        MpLocalTime.Noon.toIsoString() shouldBe "12:00:00.000"
    }

    "inWholeMilliSeconds" {
        MpLocalTime.parse("00:00:00.000").inWholeMilliSeconds() shouldBe
                (0 * 60 * 60 + 0 * 60 + 0) * 1_000 + 0

        MpLocalTime.parse("12:13:14.123").inWholeMilliSeconds() shouldBe
                (12 * 60 * 60 + 13 * 60 + 14) * 1_000 + 123

        MpLocalTime.parse("23:59:59.999").inWholeMilliSeconds() shouldBe
                (23 * 60 * 60 + 59 * 60 + 59) * 1_000 + 999
    }

    "properties - hour, minute, second, milliSecond" {
        MpLocalTime.of(hour = 12, minute = 13, second = 14, milliSecond = 123).let {
            it.hour shouldBe 12
            it.minute shouldBe 13
            it.second shouldBe 14
            it.milliSecond shouldBe 123
        }
    }

    "properties - hour - in detail" {
        (0..48).forEach { hour ->

            val subject = MpLocalTime.of(hour, 13, 14, 123)
            val expected = hour % 24

            withClue("Hours of '${subject.toIsoString()}' should be '$expected'") {
                subject.hour shouldBe expected
            }
        }
    }

    "Equality" {
        MpLocalTime.of(12, 13, 14, 123) shouldBe
                MpLocalTime.of(12, 13, 14, 123)

        MpLocalTime.of(12, 13, 14, 123) shouldNotBe
                MpLocalTime.of(12, 13, 14, 124)
    }

    "Comparison" {
        MpLocalTime.of(12, 13, 14, 123) shouldBeGreaterThan
                MpLocalTime.of(12, 13, 14, 122)

        MpLocalTime.of(12, 13, 14, 123) shouldBeEqualComparingTo
                MpLocalTime.of(12, 13, 14, 123)

        MpLocalTime.of(12, 13, 14, 123) shouldBeLessThan
                MpLocalTime.of(12, 13, 14, 124)
    }

    "toString" {
        MpLocalTime.of(12, 13, 14, 123)
            .toString() shouldBe "MpLocalTime(12:13:14.123)"
    }

    "toIsoString" {
        MpLocalTime.of(12, 13, 14, 123)
            .toIsoString() shouldBe "12:13:14.123"
    }

    "format" {
        MpLocalTime.of(12, 13, 14, 123)
            .format("HH mm ss SSS") shouldBe "12 13 14 123"
    }

    "formatHhMm" {
        MpLocalTime.of(12, 13, 14, 123)
            .formatHhMm() shouldBe "12:13"
    }

    "formatHhMmSs" {
        MpLocalTime.of(12, 13, 14, 123)
            .formatHhMmSs() shouldBe "12:13:14"
    }
})
