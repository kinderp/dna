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

/**
 * Source-compatible routing name for the cross-domain geo contract.
 *
 * New cross-domain code should import `org.traveldna.geo.contracts.GeoPoint`
 * directly. The alias prevents the extraction from forcing unrelated callers
 * to change in the same slice.
 */
typealias GeoPoint = org.traveldna.geo.contracts.GeoPoint
