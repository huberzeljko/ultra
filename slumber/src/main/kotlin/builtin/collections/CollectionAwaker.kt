package de.peekandpoke.ultra.slumber.builtin.collections

import de.peekandpoke.ultra.slumber.Awaker
import de.peekandpoke.ultra.slumber.Config
import kotlin.reflect.KType

class CollectionAwaker(
    private val innerType: KType,
    config: Config,
    private val creator: List<*>.() -> Any
) : Awaker {

    companion object {

        fun forList(innerType: KType, config: Config) =
            CollectionAwaker(innerType, config) { toList() }

        fun forMutableList(innerType: KType, config: Config) =
            CollectionAwaker(innerType, config) { toMutableList() }

        fun forSet(innerType: KType, config: Config) =
            CollectionAwaker(innerType, config) { toSet() }

        fun forMutableSet(innerType: KType, config: Config) =
            CollectionAwaker(innerType, config) { toMutableSet() }
    }

    private val itemAwaker = config.getAwaker(innerType)

    override fun awake(data: Any?): Any? {

        if (data is Array<*>) {
            return awakeInternal(data.toList())
        }

        if (data is Iterable<*>) {
            return awakeInternal(data)
        }

        return null
    }

    private fun awakeInternal(data: Iterable<*>): Any? {

        val intermediate = data.map { itemAwaker.awake(it) }

        if (innerType.isMarkedNullable) {
            return intermediate.creator()
        }

        return intermediate.filterNotNull().creator()
    }
}