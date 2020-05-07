package de.peekandpoke.ultra.slumber.builtin.datetime

import de.peekandpoke.ultra.slumber.Awaker
import de.peekandpoke.ultra.slumber.Slumberer
import java.util.*

object DateAwaker : Awaker {

    override fun awake(data: Any?, context: Awaker.Context): Date? {

        if (data !is Map<*, *>) {
            return null
        }

        return when (val ts = data[TS]) {
            is Number -> Date(ts.toLong())
            else -> null
        }
    }
}

object DateSlumberer : Slumberer {

    override fun slumber(data: Any?, context: Slumberer.Context): Map<String, Any>? {

        if (data !is Date) {
            return null
        }

        return toMap(data.time, utc, data.toString())
    }
}
