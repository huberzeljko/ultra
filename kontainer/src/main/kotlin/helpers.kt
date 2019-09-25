package de.peekandpoke.ultra.kontainer

import kotlin.reflect.KClass
import kotlin.reflect.KType

internal fun isPrimitive(cls: KClass<*>) = cls.java.isPrimitive || cls == String::class

internal fun isServiceType(cls: KClass<*>) = !isPrimitive(cls) && cls.typeParameters.isEmpty()

internal fun isListType(type: KType) =
    type.classifier == List::class && isServiceType(type.arguments[0].type!!.classifier as KClass<*>)

internal fun isLazyType(type: KType) =
    type.classifier == Lazy::class && isServiceType(type.arguments[0].type!!.classifier as KClass<*>)
