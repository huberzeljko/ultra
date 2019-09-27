package de.peekandpoke.ultra.kontainer

import kotlin.reflect.KClass

/**
 * Helper class to validate the correctness of mandatory services provided to a KontainerBlueprint.
 *
 * For each set of classes passed to [getMissing] the result is cached for future reuse.
 *
 * For each set of classes passed to [getUnexpected] the result is cached for future reuse.
 */
data class MandatoryDynamicsChecker internal constructor(
    val mandatoryDynamics: Set<KClass<*>>,
    val allDynamics: Set<KClass<*>>
) {

    /**
     * Internal cache
     *
     * A set of classes will have the same hash for the same classes, so we can do some caching here
     */
    private val missingLookUp = mutableMapOf<Set<KClass<*>>, Set<KClass<*>>>()

    /**
     * Internal cache
     *
     * A set of classes will have the same hash for the same classes, so we can do some caching here
     */
    private val unexpectedLookUp = mutableMapOf<Set<KClass<*>>, Set<KClass<*>>>()

    /**
     * Checks if [given] contains all [mandatoryDynamics]
     *
     * If all is well an empty set is returned.
     * Otherwise the set will contain all classes that are missing.
     */
    fun getMissing(given: Set<KClass<*>>): Set<KClass<*>> {

        return missingLookUp.getOrPut(given) {
            mandatoryDynamics.filter { mandatory ->
                given.none { givenClass ->
                    mandatory.java.isAssignableFrom(givenClass.java)
                }
            }.toSet()
        }
    }

    /**
     * Checks if all [given] are contained in all [allDynamics]
     *
     * If all is well an empty set is returned.
     * Otherwise the set will contain all classes that are not expected.
     */
    fun getUnexpected(given: Set<KClass<*>>): Set<KClass<*>> {

        return unexpectedLookUp.getOrPut(given) {
            given.filter { givenClass ->
                allDynamics.none { dynamic ->
                    dynamic.java.isAssignableFrom(givenClass.java)
                }
            }.toSet()
        }
    }
}
