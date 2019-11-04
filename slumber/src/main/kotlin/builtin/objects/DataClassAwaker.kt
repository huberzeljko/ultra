package de.peekandpoke.ultra.slumber.builtin.objects

import de.peekandpoke.ultra.slumber.Awaker
import de.peekandpoke.ultra.slumber.Config
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.primaryConstructor

class DataClassAwaker(type: KType, config: Config) : Awaker {

    private val cls = type.classifier as KClass<*>

    private val fqn = cls.qualifiedName

    private val ctor = cls.primaryConstructor!!

    private val ctorParams2Awakers =
        ctor.parameters.map {

            val parameterType = when {
                it.type.classifier is KTypeParameter -> {
                    // which type parameter do we have?
                    val index = cls.typeParameters.indexOf(it.type.classifier as KTypeParameter)
                    // get the real type of the type parameter
                    type.arguments[index].type!!
                }

                else -> it.type
            }

            it to config.getAwaker(parameterType)
        }

    private val nullables: Map<KParameter, Any?> =
        ctor.parameters.filter { it.type.isMarkedNullable }.map { it to null }.toMap()

    override fun awake(data: Any?): Any? {

        // Do we have some data that we can work with?
        if (data !is Map<*, *>) {
            return null
        }

        // We start with all the nullable parameters
        val params = nullables.toMutableMap()
        // We track all the missing parameters for better error reporting
        val missingParams = mutableListOf<String>()

        // We go through all the parameters of the primary ctor
        ctorParams2Awakers.forEach { (param, awaker) ->

            // Do we have data for param ?
            if (data.contains(param.name)) {
                // Get the value and awake it
                val bit = awaker.awake(data[param.name])

                // can we use the bit ?
                if (bit != null || param.type.isMarkedNullable) {
                    params[param] = bit
                } else {
                    missingParams.add(param.name ?: "n/a")
                }

                // Yes so let's awake it and put it into the params
            } else {
                // Last chance: is the parameter optional or nullable ?
                if (!param.isOptional && !param.type.isMarkedNullable) {
                    // No, so we have problem and cannot awake the data class
                    missingParams.add(param.name ?: "n/a")
                }
            }
        }

        // When we have all the parameters we need, we can call the ctor?
        if (missingParams.isEmpty()) {
            return ctor.callBy(params)
        }

        return null
    }
}