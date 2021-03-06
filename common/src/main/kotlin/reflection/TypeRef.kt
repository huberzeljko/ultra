package de.peekandpoke.ultra.common.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType

/**
 * Creates a type safe representation of the given [KType]
 *
 * [TypeRef] is a wrapper around KType. It also carries type information in [T].
 *
 * By doing so we can utilize the compiler for checking that reflection types are used correctly.
 */
@Suppress("unused")
data class TypeRef<T>(val type: KType) {

    companion object {

        val Unit = kType<Unit>()
        val UnitNull = kType<Unit?>()

        val Any = kType<Any>()
        val AnyNull = kType<Any?>()

        val Boolean = kType<Boolean>()
        val BooleanNull = kType<Boolean?>()

        val Byte = kType<Byte>()
        val ByteNull = kType<Byte?>()

        val Char = kType<Char>()
        val CharNull = kType<Char?>()

        val Double = kType<Double>()
        val DoubleNull = kType<Double?>()

        val Float = kType<Float>()
        val FloatNull = kType<Float?>()

        val Int = kType<Int>()
        val IntNull = kType<Int?>()

        val Long = kType<Long>()
        val LongNull = kType<Long?>()

        val Number = kType<Number>()
        val NumberNull = kType<Number?>()

        val Short = kType<Short>()
        val ShortNull = kType<Short?>()

        val String = kType<String>()
        val StringNull = kType<String?>()
    }

    /**
     * Returns a [ReifiedKType] of this TypeRef
     */
    val reified: ReifiedKType by lazy { ReifiedKType(type) }

    /**
     * Converts to a nullable type
     */
    val nullable: TypeRef<T?> by lazy(LazyThreadSafetyMode.NONE) {
        @Suppress("RemoveExplicitTypeArguments")
        TypeRef<T?>(
            type.classifier!!.createType(
                arguments = type.arguments,
                nullable = true
            )
        )
    }

    /**
     * Wraps the current type as a [List] type
     */
    val list: TypeRef<List<T>> by lazy(LazyThreadSafetyMode.NONE) {
        @Suppress("RemoveExplicitTypeArguments")
        TypeRef<List<T>>(
            List::class.createType(
                arguments = listOf(
                    KTypeProjection.invariant(type)
                ),
                nullable = false
            )
        )
    }

    /**
     * Wraps the current type with the given generic type [W]
     *
     * E.g. makes a String? type a MyWrapper<String?> type
     *
     * When the given type [W] does not have exactly one type parameter an exception is thrown
     */
    inline fun <reified W : Any> wrapWith(): TypeRef<W> = wrapWith(W::class, null is W)

    /**
     * Wraps the current type with the given generic type [W]
     *
     * E.g. makes a String? type a MyWrapper<String?> type
     *
     * When the given type [W] does not have exactly one type parameter an exception is thrown
     */
    @Suppress("UNCHECKED_CAST")
    fun <W : Any> wrapWith(cls: KClass<W>, nullable: Boolean): TypeRef<W> = wrapCache.getOrPut(cls) {

        if (cls.typeParameters.size != 1) {
            error("Can only wrap with a generic type with exactly one type parameter")
        }

        return TypeRef(
            cls.createType(
                arguments = listOf(
                    KTypeProjection.invariant(type)
                ),
                nullable = nullable
            )
        )
    } as TypeRef<W>

    /**
     * Internal helper val, that does not carry the correct generic type information.
     *
     * @see [unList] which takes care of the generic type information
     */
    internal val unlisted: TypeRef<Any> by lazy(LazyThreadSafetyMode.NONE) {
        if (type.classifier != List::class) {
            error("The current type [$this] must have the classifier List::class")
        }

        @Suppress("RemoveExplicitTypeArguments")
        TypeRef<Any>(type.arguments[0].type!!)
    }

    /**
     * Internal cache used by [wrapWith]
     */
    private val wrapCache = mutableMapOf<KClass<*>, TypeRef<*>>()
}

/**
 * Unwraps a List-type
 *
 * E.g. makes a List<String?>-type a String?-type.
 *
 * When the classifier of the current type is not List::class an exception is thrown.
 */
@Suppress("UNCHECKED_CAST")
val <T> TypeRef<List<T>>.unList: TypeRef<T>
    get() = unlisted as TypeRef<T>
