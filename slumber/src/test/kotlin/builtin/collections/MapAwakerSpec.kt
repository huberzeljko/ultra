package de.peekandpoke.ultra.slumber.builtin.collections

import de.peekandpoke.ultra.common.reflection.kMapType
import de.peekandpoke.ultra.common.reflection.kMutableMapType
import de.peekandpoke.ultra.slumber.AwakerException
import de.peekandpoke.ultra.slumber.Codec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class MapAwakerSpec : StringSpec({

    "Awaking a Map must work" {

        val subject = MapAwaker.forMap(kMapType<String, Int>().type)
        val codec = Codec.default
        val result = subject.awake(mapOf("a" to 1, 2 to 2), codec.secondPassAwakerContext)!!

        Map::class.java.isAssignableFrom(result::class.java) shouldBe true
        result shouldBe mapOf("a" to 1, "2" to 2)
    }

    "Awaking a Map must return null for invalid data" {

        val subject = MapAwaker.forMap(kMapType<String, Int>().type)
        val codec = Codec.default
        val result = subject.awake(listOf<Any>(), codec.secondPassAwakerContext)

        result shouldBe null
    }

    "Awaking a Map with nullable values must work" {

        val subject = MapAwaker.forMap(kMapType<String, Int?>().type)
        val codec = Codec.default
        val result = subject.awake(mapOf("a" to 1, 2 to null), codec.secondPassAwakerContext)!!

        Map::class.java.isAssignableFrom(result::class.java) shouldBe true
        result shouldBe mapOf("a" to 1, "2" to null)
    }

    "Awaking a Map with non-nullable values must fail when a null is found" {

        val subject = MapAwaker.forMap(kMapType<String, Int>().type)

        val codec = Codec.default

        val error = shouldThrow<AwakerException> {
            subject.awake(mapOf("a" to 1, "wrong" to null), codec.secondPassAwakerContext)
        }

        error.message shouldContain "root.wrong[VAL]"
    }

    "Awaking a Map (in a data class) must work" {

        data class DataClass(val items: Map<String, Int>)

        val codec = Codec.default

        val result = codec.awake(DataClass::class, mapOf("items" to mapOf("1" to "1", "2" to "2")))!!

        Map::class.java.isAssignableFrom(result.items::class.java) shouldBe true
        result.items shouldBe mapOf("1" to 1, "2" to 2)
    }

    "Awaking a MutableMap must work" {

        val subject = MapAwaker.forMap(kMutableMapType<String, Int>().type)

        val codec = Codec.default

        val result = subject.awake(mapOf("a" to 1, 2 to 2), codec.secondPassAwakerContext)!!

        MutableMap::class.java.isAssignableFrom(result::class.java) shouldBe true
        result shouldBe mapOf("a" to 1, "2" to 2)
    }

    "Awaking a MutableMap with nullable values must work" {

        val subject = MapAwaker.forMap(kMutableMapType<String, Int?>().type)

        val codec = Codec.default

        val result = subject.awake(mapOf("a" to 1, 2 to null), codec.secondPassAwakerContext)!!

        MutableMap::class.java.isAssignableFrom(result::class.java) shouldBe true
        result shouldBe mapOf("a" to 1, "2" to null)
    }

    "Awaking a MutableMap with non-nullable values must fail when a null is found" {

        val subject = MapAwaker.forMap(kMutableMapType<String, Int>().type)

        val codec = Codec.default

        val error = shouldThrow<AwakerException> {
            subject.awake(mapOf("a" to 1, "wrong" to null), codec.secondPassAwakerContext)
        }

        error.message shouldContain "root.wrong[VAL]"
    }

    "Awaking a MutableMap (in a data class) must work" {

        data class DataClass(val items: MutableMap<String, Int>)

        val codec = Codec.default

        val result = codec.awake(DataClass::class, mapOf("items" to mapOf("1" to "1", "2" to "2")))!!

        MutableMap::class.java.isAssignableFrom(result.items::class.java) shouldBe true
        result.items shouldBe mapOf("1" to 1, "2" to 2)
    }
})
