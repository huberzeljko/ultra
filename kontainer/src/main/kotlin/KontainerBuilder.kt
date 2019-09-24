package de.peekandpoke.ultra.kontainer

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.reflect

class KontainerBuilder(builder: KontainerBuilder.() -> Unit) {

    private val config = mutableMapOf<String, Any>()

    private val definitions = mutableMapOf<KClass<*>, ServiceDefinition>()

    private val definitionLocations = mutableMapOf<KClass<*>, StackTraceElement>()

    init {
        builder(this)
    }

    fun build(): KontainerBlueprint = KontainerBlueprint(config.toMap(), definitions, definitionLocations)

    // adding services ///////////////////////////////////////////////////////////////////////////////////////

    private fun add(def: ServiceDefinition) = apply {
        definitions[def.produces] = def

        // we also record the location from where a service was recorded for better error message
        definitionLocations[def.produces] = Throwable().stackTrace.first {
            it.className != KontainerBuilder::class.qualifiedName
        }
    }

    /**
     * Defines a singleton service
     */
    fun <T : Any> singleton(cls: KClass<T>) = apply {

        if (cls.java.isInterface || cls.isAbstract) {
            throw InvalidClassProvided("A singleton service cannot be an interface or abstract class")
        }

        add(
            ServiceDefinition(
                cls,
                InjectionType.Singleton,
                Producer(cls.primaryConstructor!!.parameters) { params ->
                    cls.primaryConstructor!!.call(*params)
                }
            )
        )
    }

    /**
     * Defines a singleton service
     */
    inline fun <reified T : Any> singleton() = singleton(T::class)

    /**
     * Adds a dynamic service
     */
    fun <T : Any> dynamic(cls: KClass<T>) = add(
        ServiceDefinition(cls, InjectionType.Dynamic)
    )

    /**
     * Defines a dynamic service
     */
    inline fun <reified T : Any> dynamic() = dynamic(T::class)

    /**
     * Create a singleton via a factory method with one injected parameter
     */
    fun <T : Any, P1> singleton(cls: KClass<T>, factory: (P1) -> T) = add(

        ServiceDefinition(cls, InjectionType.Singleton,
            Producer(factory.reflect()!!.parameters) { (p1) ->
                @Suppress("UNCHECKED_CAST")
                factory.invoke(p1 as P1)
            }
        )
    )

    /**
     * Create a singleton via a factory method with one injected parameter
     */
    inline fun <reified T : Any, P1> singleton(noinline factory: (P1) -> T) = singleton(T::class, factory)

    /**
     * Create a singleton via a factory method with two injected parameters
     */
    fun <T : Any, P1, P2> singleton(cls: KClass<T>, factory: (P1, P2) -> T) = add(

        ServiceDefinition(cls, InjectionType.Singleton,
            Producer(factory.reflect()!!.parameters) { (p1, p2) ->
                @Suppress("UNCHECKED_CAST")
                factory.invoke(p1 as P1, p2 as P2)
            }
        )
    )

    /**
     * Create a singleton via a factory method with two injected parameters
     */
    inline fun <reified T : Any, P1, P2> singleton(noinline factory: (P1, P2) -> T) = singleton(T::class, factory)

    /**
     * Create a singleton via a factory method with three injected parameters
     */
    fun <T : Any, P1, P2, P3> singleton(cls: KClass<T>, factory: (P1, P2, P3) -> T) = add(

        ServiceDefinition(cls, InjectionType.Singleton,
            Producer(factory.reflect()!!.parameters) { (p1, p2, p3) ->
                @Suppress("UNCHECKED_CAST")
                factory.invoke(p1 as P1, p2 as P2, p3 as P3)
            }
        )
    )

    /**
     * Create a singleton via a factory method with three injected parameters
     */
    inline fun <reified T : Any, P1, P2, P3> singleton(noinline factory: (P1, P2, P3) -> T) =
        singleton(T::class, factory)


    /**
     * Create a singleton via a factory method with four injected parameters
     */
    fun <T : Any, P1, P2, P3, P4> singleton(cls: KClass<T>, factory: (P1, P2, P3, P4) -> T) = add(

        ServiceDefinition(cls, InjectionType.Singleton,
            Producer(factory.reflect()!!.parameters) { (p1, p2, p3, p4) ->
                @Suppress("UNCHECKED_CAST")
                factory.invoke(p1 as P1, p2 as P2, p3 as P3, p4 as P4)
            }
        )
    )

    /**
     * Create a singleton via a factory method with four injected parameters
     */
    inline fun <reified T : Any, P1, P2, P3, P4> singleton(noinline factory: (P1, P2, P3, P4) -> T) =
        singleton(T::class, factory)

    /**
     * Create a singleton via a factory method with five injected parameters
     */
    fun <T : Any, P1, P2, P3, P4, P5> singleton(cls: KClass<T>, factory: (P1, P2, P3, P4, P5) -> T) = add(

        ServiceDefinition(cls, InjectionType.Singleton,
            Producer(factory.reflect()!!.parameters) { (p1, p2, p3, p4, p5) ->
                @Suppress("UNCHECKED_CAST")
                factory.invoke(p1 as P1, p2 as P2, p3 as P3, p4 as P4, p5 as P5)
            }
        )
    )

    /**
     * Create a singleton via a factory method with five injected parameters
     */
    inline fun <reified T : Any, P1, P2, P3, P4, P5> singleton(noinline factory: (P1, P2, P3, P4, P5) -> T) =
        singleton(T::class, factory)

    /**
     * Create a singleton via a factory method with six injected parameters
     */
    fun <T : Any, P1, P2, P3, P4, P5, P6> singleton(cls: KClass<T>, factory: (P1, P2, P3, P4, P5, P6) -> T) = add(

        ServiceDefinition(cls, InjectionType.Singleton,
            Producer(factory.reflect()!!.parameters) { p ->
                @Suppress("UNCHECKED_CAST")
                factory.invoke(p[0] as P1, p[1] as P2, p[2] as P3, p[3] as P4, p[4] as P5, p[5] as P6)
            }
        )
    )

    /**
     * Create a singleton via a factory method with six injected parameters
     */
    inline fun <reified T : Any, P1, P2, P3, P4, P5, P6> singleton(
        noinline factory: (P1, P2, P3, P4, P5, P6) -> T
    ) = singleton(T::class, factory)

    /**
     * Create a singleton via a factory method with six injected parameters
     */
    fun <T : Any, P1, P2, P3, P4, P5, P6, P7> singleton(
        cls: KClass<T>, factory: (P1, P2, P3, P4, P5, P6, P7) -> T
    ) = add(

        ServiceDefinition(cls, InjectionType.Singleton,
            Producer(factory.reflect()!!.parameters) { p ->
                @Suppress("UNCHECKED_CAST")
                factory.invoke(p[0] as P1, p[1] as P2, p[2] as P3, p[3] as P4, p[4] as P5, p[5] as P6, p[6] as P7)
            }
        )
    )

    /**
     * Create a singleton via a factory method with seven injected parameters
     */
    inline fun <reified T : Any, P1, P2, P3, P4, P5, P6, P7> singleton(
        noinline factory: (P1, P2, P3, P4, P5, P6, P7) -> T
    ) = singleton(T::class, factory)

    // adding config values //////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets an injectable config value
     */
    fun config(id: String, value: Int) = apply { config[id] = value }

    /**
     * Sets an injectable config value
     */
    fun config(id: String, value: Long) = apply { config[id] = value }

    /**
     * Sets an injectable config value
     */
    fun config(id: String, value: Float) = apply { config[id] = value }

    /**
     * Sets an injectable config value
     */
    fun config(id: String, value: Double) = apply { config[id] = value }

    /**
     * Sets an injectable config value
     */
    fun config(id: String, value: String) = apply { config[id] = value }

    /**
     * Sets an injectable config value
     */
    fun config(id: String, value: Boolean) = apply { config[id] = value }
}
