package de.peekandpoke.ultra.common

@Suppress("Detekt.TooGenericExceptionCaught")
inline fun <reified T : Enum<T>> safeEnumValueOf(value: String?, default: T): T {

    return when (value) {
        null -> default

        else -> try {
            enumValueOf(value)
        } catch (e: RuntimeException) {
            default
        }
    }
}

@Suppress("Detekt.TooGenericExceptionCaught")
inline fun <reified T : Enum<T>> safeEnumValueOrNull(value: String?): T? {

    return when (value) {
        null -> null

        else -> try {
            enumValueOf(value) as T
        } catch (e: RuntimeException) {
            null
        }
    }
}
