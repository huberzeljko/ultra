package de.peekandpoke.ultra.slumber.builtin

import de.peekandpoke.ultra.slumber.Slumberer

open class NonNullSlumberer(private val inner: Slumberer) : Slumberer {

    override fun slumber(data: Any?, context: Slumberer.Context): Any {
        return inner.slumber(data, context)
            ?: context.reportNullError()
    }
}
