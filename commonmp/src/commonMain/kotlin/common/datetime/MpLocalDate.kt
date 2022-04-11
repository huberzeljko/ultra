package de.peekandpoke.ultra.common.datetime

import com.soywiz.klock.Date
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Suppress("DataClassPrivateConstructor")
@Serializable(with = MpLocalDateSerializer::class)
data class MpLocalDate internal constructor(
    private val value: LocalDate,
) : Comparable<MpLocalDate> {

    companion object {
        /**
         * Creates an [MpLocalDate] from [year], [month] and [day].
         */
        fun of(year: Int, month: Int, day: Int): MpLocalDate = MpLocalDate(
            value = LocalDate(
                year = year,
                monthNumber = month,
                dayOfMonth = day,
            )
        )

        /**
         * Creates an [MpLocalDate] from [year], [month] and [day].
         */
        fun of(year: Int, month: Month, day: Int): MpLocalDate = of(
            year = year,
            month = month.number,
            day = day,
        )

        /**
         * Creates an [MpLocalDate] from the given [isoString].
         */
        fun parse(isoString: String): MpLocalDate {

            return try {
                MpLocalDate(
                    value = LocalDate.parse(isoString)
                )
            } catch (e: Throwable) {
                MpInstant.parse(isoString).atZone(TimeZone.UTC).toLocalDate()
            }
        }

        /**
         * The Genesis, a date in the distant past: -10000-01-01T00:00:00Z
         */
        val Genesis: MpLocalDate = MpLocalDate(
            Instant.fromEpochMilliseconds(GENESIS_TIMESTAMP).toLocalDateTime(TimeZone.UTC).date
        )

        /**
         * The Doomsday, a date in the distant future: +10000-01-01T00:00:00Z
         */
        val Doomsday: MpLocalDate = MpLocalDate(
            Instant.fromEpochMilliseconds(DOOMSDAY_TIMESTAMP).toLocalDateTime(TimeZone.UTC).date
        )
    }

    /** The year */
    val year: Int get() = value.year

    /** The month as number, where January is "1" */
    val monthNumber: Int get() = value.monthNumber

    /** The month */
    val month: Month get() = value.month

    /** The day of the month */
    val day: Int get() = value.dayOfMonth

    /** The day of the week */
    val dayOfWeek: DayOfWeek get() = value.dayOfWeek

    /** The day of the year */
    val dayOfYear: Int get() = value.dayOfYear

    /**
     * Compares to the [other].
     */
    override fun compareTo(other: MpLocalDate): Int {
        return value.compareTo(other.value)
    }

    /**
     * Converts into a string.
     */
    override fun toString(): String {
        return "MpLocalDate(${toIsoString()})"
    }

    /**
     * Converts into a human-readable string by the given [formatString].
     *
     * See https://help.gooddata.com/cloudconnect/manual/date-and-time-format.html
     */
    fun format(formatString: String): String {
        val klock = Date(year = year, month = monthNumber, day = day)

        return klock.format(formatString)
    }

    /**
     * Converts into an iso string.
     */
    fun toIsoString(): String {
        return atStartOfDay(TimeZone.UTC).toIsoString()
    }

    /**
     * Gets the start of the month
     */
    fun atStartOfYear(): MpLocalDate {
        return of(year = year, month = Month.JANUARY, day = 1)
    }

    /**
     * Gets the start of the month
     */
    fun atStartOfMonth(): MpLocalDate {
        return of(year = year, month = month, day = 1)
    }

    /**
     * Gets the upcoming day that is of the given [dayOfWeek].
     */
    fun atStartOfNext(dayOfWeek: DayOfWeek): MpLocalDate {

        var result = plus(1, DateTimeUnit.DAY)

        while (result.dayOfWeek != dayOfWeek) {
            result = result.plus(1, DateTimeUnit.DAY)
        }

        return result
    }

    /**
     * Gets the previous day that is of the given [dayOfWeek].
     */
    fun atStartOfPrevious(dayOfWeek: DayOfWeek): MpLocalDate {

        var result = this

        while (result.dayOfWeek != dayOfWeek) {
            result = result.minus(1, DateTimeUnit.DAY)
        }

        return result
    }

    /**
     * Converts into an [MpZonedDateTime] at the given [timezone].
     */
    fun atStartOfDay(timezone: MpTimezone): MpZonedDateTime {
        return atStartOfDay(timezone.kotlinx)
    }

    /**
     * Converts into an [MpZonedDateTime] at the given [timezone].
     */
    fun atStartOfDay(timezone: TimeZone): MpZonedDateTime {
        return MpInstant(value.atStartOfDayIn(timezone)).atZone(timezone)
    }

    /**
     * Converts into an [MpLocalDateTime] by setting the given [time].
     */
    fun atTime(time: MpLocalTime): MpLocalDateTime {
        return MpLocalDateTime.of(date = this, time = time)
    }

    /**
     * Converts into an [MpZonedDateTime] in the given [timezone] by setting the given [time].
     */
    fun atTime(time: MpLocalTime, timezone: TimeZone): MpZonedDateTime {
        return atStartOfDay(timezone).atTime(time)
    }

    /**
     * Converts into an [MpZonedDateTime] in the given [timezone] by setting the given [time].
     */
    fun atTime(time: MpLocalTime, timezone: MpTimezone): MpZonedDateTime {
        return atTime(time, timezone.kotlinx)
    }

    /**
     * Converts into an [MpLocalDateTime] at the start of the day.
     */
    fun toLocalDateTime(): MpLocalDateTime {
        return atTime(MpLocalTime.Min)
    }

    /**
     * Adds the given [unit] once.
     */
    fun plus(unit: DateTimeUnit.DateBased): MpLocalDate {
        return MpLocalDate(
            value = value.plus(1, unit)
        )
    }

    /**
     * Adds [amount] times the given [unit].
     */
    fun plus(amount: Int, unit: DateTimeUnit.DateBased): MpLocalDate {
        return MpLocalDate(
            value = value.plus(amount, unit)
        )
    }

    /**
     * Adds [amount] times the given [unit].
     */
    fun plus(amount: Long, unit: DateTimeUnit.DateBased): MpLocalDate {
        return MpLocalDate(
            value = value.plus(amount, unit)
        )
    }

    /**
     * Adds the given [DatePeriod].
     */
    fun plus(period: DatePeriod): MpLocalDate {
        return MpLocalDate(
            value = value.plus(period)
        )
    }

    /**
     * Subtracts the given [unit] once.
     */
    fun minus(unit: DateTimeUnit.DateBased): MpLocalDate {
        return MpLocalDate(
            value = value.minus(1, unit)
        )
    }

    /**
     * Subtracts [amount] times the given [unit].
     */
    fun minus(amount: Int, unit: DateTimeUnit.DateBased): MpLocalDate {
        return MpLocalDate(
            value = value.minus(amount, unit)
        )
    }

    /**
     * Subtracts [amount] times the given [unit].
     */
    fun minus(amount: Long, unit: DateTimeUnit.DateBased): MpLocalDate {
        return MpLocalDate(
            value = value.minus(amount, unit)
        )
    }

    /**
     * Adds the given [DatePeriod].
     */
    fun minus(period: DatePeriod): MpLocalDate {
        return MpLocalDate(
            value = value.plus(period)
        )
    }
}

/**
 * Formats the date as 'dd MMM yyyy'
 */
fun MpLocalDate.formatDdMmmYyyy(): String {
    return format("dd MMM yyyy")
}

