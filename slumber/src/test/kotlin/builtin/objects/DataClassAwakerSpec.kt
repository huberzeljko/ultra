package de.peekandpoke.ultra.slumber.builtin.objects

import de.peekandpoke.ultra.slumber.AwakerException
import de.peekandpoke.ultra.slumber.Codec
import de.peekandpoke.ultra.slumber.Shared
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.withClue
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

class DataClassAwakerSpec : StringSpec({

    "Awaking a data class with two parameters" {

        data class DataClass(val str: String, val int: Int)

        val codec = Codec(Shared())

        val result = codec.awake(DataClass::class, mapOf("str" to "hello", "int" to 1))

        result shouldBe DataClass("hello", 1)
    }

    "Awaking a data class with one String parameter" {

        data class DataClass(val str: String)

        val codec = Codec(Shared())

        val result = codec.awake(DataClass::class, mapOf("str" to "hello"))

        result shouldBe DataClass("hello")
    }

    "Awaking a data class with one String? parameter" {

        data class DataClass(val str: String?)

        val codec = Codec(Shared())

        assertSoftly {

            withClue("A given value must be set") {
                codec.awake(DataClass::class, mapOf("str" to "hello")) shouldBe DataClass("hello")
            }

            withClue("A given null value must be set") {
                codec.awake(DataClass::class, mapOf("str" to null)) shouldBe DataClass(null)
            }

            withClue("A missing value must be used like null") {
                codec.awake(DataClass::class, emptyMap<String, Any>()) shouldBe DataClass(null)
            }

            withClue("Any data that is not a Map is not acceptable even when all fields are nullable") {
                shouldThrow<AwakerException> {
                    codec.awake(DataClass::class, null)
                }

                shouldThrow<AwakerException> {
                    codec.awake(DataClass::class, "invalid")
                }
            }
        }
    }

    "Awaking a data class with one default parameter with value present" {

        data class DataClass(val str: String = "default")

        val codec = Codec(Shared())

        assertSoftly {
            withClue("A given value must overwrite the default value") {
                codec.awake(DataClass::class, mapOf("str" to "hello")) shouldBe DataClass("hello")
            }

            withClue("When no value is given the default value must be used") {
                codec.awake(DataClass::class, emptyMap<String, Any>()) shouldBe DataClass("default")
            }

            withClue("Null is not acceptable for non-nullable parameters even when optional") {
                shouldThrow<AwakerException> {
                    codec.awake(DataClass::class, mapOf("str" to null))
                }
            }

            withClue("Any data that is not a Map is not acceptable even when all fields are optional") {
                shouldThrow<AwakerException> {
                    codec.awake(DataClass::class, null)
                }

                shouldThrow<AwakerException> {
                    codec.awake(DataClass::class, "invalid")
                }
            }
        }
    }

    "Awaking a data class with one data class parameter" {

        data class Inner(val str: String)

        data class DataClass(val inner: Inner)

        val codec = Codec(Shared())

        val result = codec.awake(DataClass::class, mapOf("inner" to mapOf("str" to "hello")))

        result shouldBe DataClass(Inner("hello"))
    }

    "Awaking a data class with one Iterable<String> parameter" {

        data class DataClass(val strings: Iterable<String>)

        val codec = Codec(Shared())

        assertSoftly {
            codec.awake(DataClass::class, mapOf("strings" to listOf("hello", "you"))) shouldBe
                    DataClass(listOf("hello", "you"))

            codec.awake(DataClass::class, mapOf("strings" to arrayOf("hello", "you"))) shouldBe
                    DataClass(listOf("hello", "you"))
        }
    }

    "Awaking a data class with one List<String> parameter" {

        data class DataClass(val strings: List<String>)

        val codec = Codec(Shared())

        assertSoftly {
            codec.awake(DataClass::class, mapOf("strings" to listOf("hello", "you"))) shouldBe
                    DataClass(listOf("hello", "you"))

            codec.awake(DataClass::class, mapOf("strings" to arrayOf("hello", "you"))) shouldBe
                    DataClass(listOf("hello", "you"))
        }
    }

    "Awaking a data class with one List<String> parameter mixin in nulls" {

        data class DataClass(val strings: List<String>)

        val codec = Codec(Shared())

        assertSoftly {
            codec.awake(DataClass::class, mapOf("strings" to listOf("hello", null))) shouldBe
                    DataClass(listOf("hello"))
        }
    }

    "Awaking a data class with one MutableList<String> parameter" {

        data class DataClass(val strings: MutableList<String>)

        val codec = Codec(Shared())

        assertSoftly {
            codec.awake(DataClass::class, mapOf("strings" to listOf("hello", "you"))) shouldBe
                    DataClass(mutableListOf("hello", "you"))

            codec.awake(DataClass::class, mapOf("strings" to arrayOf("hello", "you"))) shouldBe
                    DataClass(mutableListOf("hello", "you"))
        }
    }

    "Awaking a data class with one Set<String> parameter" {

        data class DataClass(val strings: Set<String>)

        val codec = Codec(Shared())

        assertSoftly {
            codec.awake(DataClass::class, mapOf("strings" to listOf("hello", "you"))) shouldBe
                    DataClass(setOf("hello", "you"))

            codec.awake(DataClass::class, mapOf("strings" to arrayOf("hello", "you"))) shouldBe
                    DataClass(setOf("hello", "you"))
        }
    }

    "Awaking a data class with one MutableSet<String> parameter" {

        data class DataClass(val strings: MutableSet<String>)

        val codec = Codec(Shared())

        assertSoftly {
            codec.awake(DataClass::class, mapOf("strings" to listOf("hello", "you"))) shouldBe
                    DataClass(mutableSetOf("hello", "you"))

            codec.awake(DataClass::class, mapOf("strings" to arrayOf("hello", "you"))) shouldBe
                    DataClass(mutableSetOf("hello", "you"))
        }
    }
})
