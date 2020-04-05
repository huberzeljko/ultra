package de.peekandpoke.ultra.kontainer.examples._02_injection

import de.peekandpoke.ultra.common.docs.SimpleExample
import de.peekandpoke.ultra.kontainer.kontainer

@Suppress("ClassName")
class E01_BasicInjection : SimpleExample() {

    override val title = "Basic Injection Example"

    override val description = """
        This example shows how a service can inject another service.
        
        For simplicity there is only constructor injection. Nothing else.
    """.trimIndent()

    override fun run() {
        // !BEGIN! //

        // 1. We define a service that will be injected
        class Counter {
            private var count = 0
            fun next() = ++count
        }

        // 2. We define a service that injects another service in it's constructor
        class MyService(val counter: Counter)

        // 3. We define the kontainer blueprint
        val blueprint = kontainer {
            singleton(MyService::class)
            singleton(Counter::class)
        }

        // 3. We get the kontainer instance
        val kontainer = blueprint.create()

        // 4. We use the service and access the injected service
        val myService = kontainer.get(MyService::class)

        println("Next: " + myService.counter.next())
        println("Next: " + myService.counter.next())

        // !END! //
    }
}

