package de.peekandpoke.ultra.kontainer

import kotlin.reflect.KClass

abstract class TypeLookup {

    /**
     * Get all matching types for the given [type]
     */
    abstract fun getAllCandidatesFor(type: KClass<*>): Set<KClass<*>>

    /**
     * Gets a a distinct super type for the given [baseType]
     *
     * If there is no super type or more than one found, a [ServiceNotFound] is thrown.
     */
    fun getDistinctFor(baseType: KClass<*>): KClass<*> {

        val candidates = getAllCandidatesFor(baseType)

        if (candidates.isEmpty()) {
            throw ServiceNotFound("Service ${baseType.qualifiedName} was not found")
        }

        if (candidates.size > 1) {
            throw ServiceAmbiguous(
                "Service ${baseType.qualifiedName} is ambiguous. It has multiple candidate: "
                        + candidates.map { it.qualifiedName }.joinToString(", ")
            )
        }

        return candidates.first()
    }


    /**
     * Helper class for finding [baseTypes] of a given super type
     *
     * Result to calls of [getAllCandidatesFor] and cached internally for future reuse.
     */
    data class ForBaseTypes(val baseTypes: Set<KClass<*>>) : TypeLookup() {

        private val cache = mutableMapOf<KClass<*>, Set<KClass<*>>>()

        /**
         * Gets all [baseTypes] that are base types of the given super [type]
         */
        override fun getAllCandidatesFor(type: KClass<*>): Set<KClass<*>> = cache.getOrPut(type) {
            baseTypes.filter { baseType ->
                baseType.java.isAssignableFrom(type.java)
            }.toSet()
        }
    }

    /**
     * Helper class for finding [superTypes] of given base type
     *
     * Result to calls of [getAllCandidatesFor] and cached internally for future reuse.
     */
    data class ForSuperTypes(val superTypes: Set<KClass<*>>) : TypeLookup() {

        private val candidatesCache = mutableMapOf<KClass<*>, Set<KClass<*>>>()

        private val lookupCache = mutableMapOf<KClass<*>, LazyServiceLookupBlueprint<*>>()

        /**
         * Gets all [superTypes] for the given base [type]
         */
        override fun getAllCandidatesFor(type: KClass<*>): Set<KClass<*>> = candidatesCache.getOrPut(type) {
            superTypes.filter { superType ->
                type.java.isAssignableFrom(superType.java)
            }.toSet()
        }

        /**
         * Get all [superTypes] for the given base [type] as a [LazyServiceLookupBlueprint]
         */
        fun getLookupBlueprint(type: KClass<*>): LazyServiceLookupBlueprint<*> = lookupCache.getOrPut(type) {

            val map = getAllCandidatesFor(type)
                .map { it to { container: Kontainer -> container.get(it) } }
                .toMap()

            LazyServiceLookupBlueprint(map)
        }
    }
}
