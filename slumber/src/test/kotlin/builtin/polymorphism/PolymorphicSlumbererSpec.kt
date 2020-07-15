package de.peekandpoke.ultra.slumber.builtin.polymorphism

import de.peekandpoke.ultra.common.reflection.kListType
import de.peekandpoke.ultra.common.reflection.kMapType
import de.peekandpoke.ultra.slumber.Codec
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class PolymorphicSlumbererSpec : StringSpec({

    ////  Directly slumbering polymorphic children  ////////////////////////////////////////////////////////////////////

    "Slumbering a polymorphic child independently must NOT include the discriminator" {

        val codec = Codec.default

        val result = codec.slumber(PureBase.A("hello"))

        assertSoftly {
            result shouldBe mapOf("text" to "hello")
        }
    }

    ////  pure sealed class and pure sealed children  //////////////////////////////////////////////////////////////////

    "Slumbering a list of sealed class children (PureBase)" {

        val codec = Codec.default

        val data = listOf(
            PureBase.A("hello"),
            PureBase.B(100)
        )

        val result = codec.slumber(kListType<PureBase>().type, data)

        assertSoftly {
            result shouldBe listOf(
                mapOf(
                    "_type" to PureBase.A::class.qualifiedName,
                    "text" to "hello"
                ),
                mapOf(
                    "_type" to PureBase.B::class.qualifiedName,
                    "number" to 100
                )
            )
        }
    }

    "Slumbering a map of string to sealed class children (PureBase)" {

        val codec = Codec.default

        val data = mapOf(
            "A" to PureBase.A("hello"),
            "B" to PureBase.B(100)
        )

        val result = codec.slumber(kMapType<String, PureBase>().type, data)

        assertSoftly {
            result shouldBe mapOf(
                "A" to mapOf(
                    "_type" to PureBase.A::class.qualifiedName,
                    "text" to "hello"
                ),
                "B" to mapOf(
                    "_type" to PureBase.B::class.qualifiedName,
                    "number" to 100
                )
            )
        }
    }

    ////  Sealed parent with custom discriminator  /////////////////////////////////////////////////////////////////////

    "Slumbering a list of sealed class children (CustomDiscriminator) for parent with custom discriminator" {

        val codec = Codec.default

        val data = listOf(
            CustomDiscriminator.A("hello"),
            CustomDiscriminator.B(100)
        )

        val result = codec.slumber(kListType<CustomDiscriminator>().type, data)

        assertSoftly {
            result shouldBe listOf(
                mapOf(
                    "_" to CustomDiscriminator.A.identifier,
                    "text" to "hello"
                ),
                mapOf(
                    "_" to CustomDiscriminator.B.identifier,
                    "number" to 100
                )
            )
        }
    }

    ////  Pure sealed class with annotated children  ///////////////////////////////////////////////////////////////////

    "Slumbering a list of sealed class children with custom type identifiers (AnnotedChildrenBase)" {

        val codec = Codec.default

        val data = listOf(
            AnnotedChildrenBase.A("hello"),
            AnnotedChildrenBase.B(100)
        )

        val result = codec.slumber(kListType<AnnotedChildrenBase>().type, data)

        assertSoftly {
            result shouldBe listOf(
                mapOf(
                    "_type" to "Child_A",
                    "text" to "hello"
                ),
                mapOf(
                    "_type" to "Child_B",
                    "number" to 100
                )
            )
        }
    }

    ////  Non sealed base class annotated with Polymorphic.Parent  /////////////////////////////////////////////////////

    "Slumbering a list of children of a non sealed parent class (AnnotatedBase)" {

        val codec = Codec.default

        val data = listOf(
            AnnotatedBase.A("hello"),
            AnnotatedBase.B(100)
        )

        val result = codec.slumber(kListType<AnnotatedBase>().type, data)

        assertSoftly {
            result shouldBe listOf(
                mapOf(
                    "_type" to AnnotatedBase.A::class.qualifiedName,
                    "text" to "hello"
                ),
                mapOf(
                    "_type" to "Child_B",
                    "number" to 100
                )
            )
        }
    }

    ////  Direct slumbering of a Polymorphic.Child  /////////////////////////////////////////////////////

    "Directly slumbering a polymorphic child must include the discriminator" {

        val codec = Codec.default

        val data = AnnotatedBase.B(number = 111)

        val result = codec.slumber(data)

        result shouldBe mapOf(
            "_type" to AnnotatedBase.B.identifier,
            "number" to 111
        )
    }

    "Directly slumbering a polymorphic with custom discriminator child must include the discriminator" {

        val codec = Codec.default

        val data = CustomDiscriminator.B(number = 111)

        val result = codec.slumber(data)

        result shouldBe mapOf(
            "_" to CustomDiscriminator.B.identifier,
            "number" to 111
        )
    }
})
