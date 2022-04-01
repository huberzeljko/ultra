package de.peekandpoke.ultra.common.datetime

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json

@Suppress("unused")
class MpInstantSerializationSpec : StringSpec({

    val json = Json

    @kotlinx.serialization.Serializable
    data class Wrapper(val date: MpInstant)

    "Serializing - pure" {

        val result = json.encodeToString(
            MpInstant.serializer(),
            MpLocalDate.of(2022, Month.APRIL, 5).atStartOfDay(TimeZone.UTC),
        )

        result shouldBe """
            {"ts":1649116800000,"timezone":"UTC","human":"2022-04-05T00:00:00Z"}
        """.trimIndent()
    }

    "Serializing - wrapped" {

        val result = json.encodeToString(
            Wrapper.serializer(),
            Wrapper(
                MpLocalDate.of(2022, Month.APRIL, 5).atStartOfDay(TimeZone.UTC),
            )
        )

        result shouldBe """
            {"date":{"ts":1649116800000,"timezone":"UTC","human":"2022-04-05T00:00:00Z"}}
        """.trimIndent()
    }

    "De-Serializing - pure" {

        val result = json.decodeFromString(
            MpInstant.serializer(),
            """
                {"ts":1649116800000,"timezone":"UTC","human":"2022-04-05T00:00:00Z"}
            """.trimIndent()
        )

        result shouldBe MpLocalDate.of(2022, Month.APRIL, 5).atStartOfDay(TimeZone.UTC)
    }

    "De-Serializing - wrapped" {

        val result = json.decodeFromString(
            Wrapper.serializer(),
            """
                {"date":{"ts":1649116800000,"timezone":"UTC","human":"2022-04-05T00:00:00Z"}}
            """.trimIndent()
        )

        result shouldBe Wrapper(
            MpLocalDate.of(2022, Month.APRIL, 5).atStartOfDay(TimeZone.UTC),
        )
    }
})
