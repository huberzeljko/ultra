package de.peekandpoke.ultra.kontainer

import de.peekandpoke.ultra.common.Lookup
import kotlin.reflect.KClass

/**
 * The container
 */
class Kontainer internal constructor(
    internal val blueprint: KontainerBlueprint,
    internal val providers: Map<KClass<*>, ServiceProvider>
) {

    // getting services ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get all service classes that would satisfy the given [cls]
     */
    fun <T : Any> getCandidates(cls: KClass<T>): Set<KClass<*>> = blueprint.superTypeLookup.getAllCandidatesFor(cls)

    /**
     * Get a service for the given class
     */
    inline fun <reified T : Any> get() = get(T::class)

    /**
     * Get a service for the given class
     */
    fun <T : Any> get(cls: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return getProvider(cls).provide(this) as T
    }

    /**
     * Get a service for the given [cls] or null if no service can be provided
     */
    fun <T : Any> getOrNull(cls: KClass<T>): T? {

        val type = blueprint.superTypeLookup.getDistinctForOrNull(cls) ?: return null

        return get(type)
    }

    /**
     * Get a service for the given [cls], and when it is present run the [block] on it.
     *
     * When the service is not present null is returned.
     * Otherwise the result of the [block] is returned.
     */
    fun <T : Any, R> use(cls: KClass<T>, block: T.() -> R?): R? {

        val type = blueprint.superTypeLookup.getDistinctForOrNull(cls) ?: return null

        return get(type).block()
    }

    /**
     * Get all services that are a super type of the given class
     */
    fun <T : Any> getAll(cls: KClass<T>): List<T> {
        @Suppress("UNCHECKED_CAST")
        return blueprint.superTypeLookup.getAllCandidatesFor(cls).map { get(it) as T }
    }

    /**
     * Get all services that are a super type of the given class as a [Lookup]
     */
    fun <T : Any> getLookup(cls: KClass<T>): Lookup<T> {

        @Suppress("UNCHECKED_CAST")
        return blueprint.superTypeLookup.getLookupBlueprint(cls).with(this) as Lookup<T>
    }

    /**
     * Get a provider for the given service class
     */
    inline fun <reified T : Any> getProvider(): ServiceProvider = getProvider(T::class)

    /**
     * Get a provider for the given service class
     */
    fun <T : Any> getProvider(cls: KClass<T>): ServiceProvider {

        val type = blueprint.superTypeLookup.getDistinctFor(cls)

        return providers.getValue(type)
    }

    // getting parameters //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Check if there is a config value with the given [id] that is of the given [type]
     */
    fun hasConfig(id: String, type: KClass<*>) = blueprint.config[id].let { it != null && it::class == type }

    /**
     * Get a config value by its [id]
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getConfig(id: String): T = blueprint.config[id] as T

    // debug ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun dump(): String {

        val maxServiceNameLength = providers.map { (k, _) -> (k.qualifiedName ?: "").length }.max() ?: 0

        val maxTypeLength = ServiceProvider.Type.values().map { it.toString().length }.max() ?: 0

        val services = providers.map { (k, v) ->
            "${(k.qualifiedName ?: "n/a").padEnd(maxServiceNameLength)} | " +
                    "${v.type.toString().padEnd(maxTypeLength)} | " +
                    if (v.createdAt != null) v.createdAt.toString() else "not created"
        }

        val maxConfigLength = blueprint.config.map { (k, _) -> k.length }.max() ?: 0

        val configs = blueprint.config
            .map { (k, v) -> "${k.padEnd(maxConfigLength)} | $v (${v::class.qualifiedName})" }
            .joinToString("\n")

        return "Kontainer dump:\n\n" +
                services.joinToString("\n") + "\n\n" +
                "Config values\n\n" +
                configs
    }
}
