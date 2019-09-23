package de.peekandpoke.ultra.kontainer.e2e

data class ConfigIntInjecting(val configInt: Int)

data class ConfigAllInjecting(
    val int: Int,
    val long: Long,
    val float: Float,
    val double: Double,
    val string: String,
    val boolean: Boolean
)

open class SimpleService {
    private var counter = 0

    fun get() = counter

    fun inc() = counter++
}

class SuperSimpleService : SimpleService()

class AnotherSimpleService

class SomeIndependentService

data class InjectingService(val simple: SimpleService, val another: AnotherSimpleService)

data class AnotherInjectingService(val other: AnotherSimpleService)

data class DeeperInjectingService(val injecting: InjectingService)

interface Ambiguous

class AmbiguousImplOne : Ambiguous

class AmbiguousImplTwo : Ambiguous

data class InjectingAmbiguous(val ambiguous: Ambiguous)

data class InjectingSomethingWeird(val weird: Map<String, String>)
