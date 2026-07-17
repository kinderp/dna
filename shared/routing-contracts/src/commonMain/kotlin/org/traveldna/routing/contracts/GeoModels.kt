package org.traveldna.routing.contracts

import kotlin.jvm.JvmInline

private val STABLE_ID = Regex("[A-Za-z0-9][A-Za-z0-9._:-]{0,127}")

internal fun requireStableId(value: String, field: String) {
    require(STABLE_ID.matches(value)) { "$field contains unsupported characters" }
}

@JvmInline
value class RouteId(val value: String) {
    init { requireStableId(value, "route id") }
    override fun toString(): String = value
}

data class GeoPoint(val latitude: Double, val longitude: Double) {
    init {
        require(latitude.isFinite() && latitude in -90.0..90.0) {
            "latitude must be finite and within [-90, 90]"
        }
        require(longitude.isFinite() && longitude in -180.0..180.0) {
            "longitude must be finite and within [-180, 180]"
        }
    }
}
