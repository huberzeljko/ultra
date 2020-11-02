package de.peekandpoke.ultra.kontainer.e2e

import de.peekandpoke.ultra.kontainer.*
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class PrototypeFactoriesSpec : StringSpec({

    abstract class Base {
        abstract val value: Int
    }

    data class Config(override val value: Int) : Base()

    fun validateWithoutBase(subject: Kontainer, value: Int) {

        assertSoftly {
            subject.get<Config>() shouldNotBeSameInstanceAs subject.get<Config>()

            subject.get<Base>()::class shouldBe Config::class
            subject.get<Config>()::class shouldBe Config::class
            subject.getProvider<Config>().type shouldBe ServiceProvider.Type.Prototype

            subject.get<Config>().value shouldBe value
        }
    }

    fun validateWithBase(subject: Kontainer, value: Int) {

        assertSoftly {
            shouldThrow<ServiceNotFound> {
                subject.get(Config::class)
            }

            subject.get<Base>() shouldNotBeSameInstanceAs subject.get<Base>()

            subject.get<Base>()::class shouldBe Config::class
            subject.getProvider<Base>().type shouldBe ServiceProvider.Type.Prototype

            subject.get<Base>().value shouldBe value
        }
    }

    "Prototype factory that has missing dependencies" {

        val blueprint = kontainer {
            singleton<SimpleService>()

            prototype { simple: SimpleService, another: AnotherSimpleService ->
                InjectingService(simple, another)
            }
        }

        assertSoftly {
            val error = shouldThrow<KontainerInconsistent> {
                blueprint.useWith()
            }

            error.message shouldContain
                    "Parameter 'another' misses a dependency to '${AnotherSimpleService::class.qualifiedName}'"

            error.message shouldContain
                    "defined at"
        }
    }

    "Prototype factory with two dependencies" {

        val subject = kontainer {
            singleton<SimpleService>()
            singleton<AnotherSimpleService>()

            prototype(InjectingService::class) { simple: SimpleService, another: AnotherSimpleService ->
                InjectingService(simple, another)
            }
        }.useWith()

        assertSoftly {
            subject.get<InjectingService>() shouldNotBeSameInstanceAs subject.get<InjectingService>()

            subject.get<InjectingService>()::class shouldBe InjectingService::class
        }
    }

    "Prototype factory with zero params" {

        val subject = kontainer {
            prototype0 { Config(0) }
        }.useWith()

        validateWithoutBase(subject, 0)
    }

    "Prototype factory with zero params registered with base class" {

        val subject = kontainer {
            prototype0(Base::class) { Config(0) }
        }.useWith()

        validateWithBase(subject, 0)
    }

    "Prototype factory with one param" {

        val subject = kontainer {
            config("c1", 1)
            prototype { c1: Int -> Config(c1) }
        }.useWith()

        validateWithoutBase(subject, 1)
    }

    "Prototype factory with one param registered with base class" {

        val subject = kontainer {
            config("c1", 1)
            prototype(Base::class) { c1: Int -> Config(c1) }
        }.useWith()

        validateWithBase(subject, 1)
    }

    "Prototype factory with two params" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            prototype { c1: Int, c2: Int -> Config(c1 + c2) }
        }.useWith()

        validateWithoutBase(subject, 11)
    }

    "Prototype factory with two params registered with base class" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            prototype(Base::class) { c1: Int, c2: Int -> Config(c1 + c2) }
        }.useWith()

        validateWithBase(subject, 11)
    }

    "Prototype factory with three params" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            prototype { c1: Int, c2: Int, c3: Int -> Config(c1 + c2 + c3) }
        }.useWith()

        validateWithoutBase(subject, 111)
    }

    "Prototype factory with three params registered with base class" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            prototype(Base::class) { c1: Int, c2: Int, c3: Int -> Config(c1 + c2 + c3) }
        }.useWith()

        validateWithBase(subject, 111)
    }

    "Prototype factory with four params" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            config("c4", 1000)
            prototype { c1: Int, c2: Int, c3: Int, c4: Int -> Config(c1 + c2 + c3 + c4) }
        }.useWith()

        validateWithoutBase(subject, 1111)
    }

    "Prototype factory with four params registered with base class" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            config("c4", 1000)
            prototype(Base::class) { c1: Int, c2: Int, c3: Int, c4: Int -> Config(c1 + c2 + c3 + c4) }
        }.useWith()

        validateWithBase(subject, 1111)
    }

    "Prototype factory with five params" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            config("c4", 1000)
            config("c5", 10000)
            prototype { c1: Int, c2: Int, c3: Int, c4: Int, c5: Int ->
                Config(c1 + c2 + c3 + c4 + c5)
            }
        }.useWith()

        validateWithoutBase(subject, 11111)
    }

    "Prototype factory with five params registered with base class" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            config("c4", 1000)
            config("c5", 10000)
            prototype(Base::class) { c1: Int, c2: Int, c3: Int, c4: Int, c5: Int ->
                Config(c1 + c2 + c3 + c4 + c5)
            }
        }.useWith()

        validateWithBase(subject, 11111)
    }

    "Prototype factory with six params" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            config("c4", 1000)
            config("c5", 10000)
            config("c6", 100000)
            prototype { c1: Int, c2: Int, c3: Int, c4: Int, c5: Int, c6: Int ->
                Config(c1 + c2 + c3 + c4 + c5 + c6)
            }
        }.useWith()

        validateWithoutBase(subject, 111111)
    }

    "Prototype factory with six params registered with base class" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            config("c4", 1000)
            config("c5", 10000)
            config("c6", 100000)
            prototype(Base::class) { c1: Int, c2: Int, c3: Int, c4: Int, c5: Int, c6: Int ->
                Config(c1 + c2 + c3 + c4 + c5 + c6)
            }
        }.useWith()

        validateWithBase(subject, 111111)
    }

    "Prototype factory with seven params" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            config("c4", 1000)
            config("c5", 10000)
            config("c6", 100000)
            config("c7", 1000000)
            prototype { c1: Int, c2: Int, c3: Int, c4: Int, c5: Int, c6: Int, c7: Int ->
                Config(c1 + c2 + c3 + c4 + c5 + c6 + c7)
            }
        }.useWith()

        validateWithoutBase(subject, 1111111)
    }

    "Prototype factory with seven params registered with base class" {

        val subject = kontainer {
            config("c1", 1)
            config("c2", 10)
            config("c3", 100)
            config("c4", 1000)
            config("c5", 10000)
            config("c6", 100000)
            config("c7", 1000000)
            prototype(Base::class) { c1: Int, c2: Int, c3: Int, c4: Int, c5: Int, c6: Int, c7: Int ->
                Config(c1 + c2 + c3 + c4 + c5 + c6 + c7)
            }
        }.useWith()

        validateWithBase(subject, 1111111)
    }
})
