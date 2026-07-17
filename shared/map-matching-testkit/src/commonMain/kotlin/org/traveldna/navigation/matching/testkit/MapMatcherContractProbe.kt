package org.traveldna.navigation.matching.testkit

import org.traveldna.location.contracts.LocationSample
import org.traveldna.navigation.matching.contracts.MapMatchResult
import org.traveldna.navigation.matching.contracts.MapMatcherPort
import org.traveldna.navigation.matching.contracts.MapMatchingCapabilities
import org.traveldna.navigation.matching.contracts.requireMatches
import org.traveldna.routing.contracts.RoutePlan

data class MapMatcherContractReport(
    val providerId: String,
    val checks: List<String>,
)

/** Reusable conformance probe for deterministic route-constrained matchers. */
object MapMatcherContractProbe {
    suspend fun verify(
        matcher: MapMatcherPort,
        route: RoutePlan,
        matchedSample: LocationSample,
        unmatchedSample: LocationSample,
    ): MapMatcherContractReport {
        val checks = mutableListOf<String>()
        require(MapMatchingCapabilities.MatchRoute in matcher.descriptor.capabilities) {
            "map matcher must declare navigation.map-match"
        }
        checks += "declares-map-match"

        val session = matcher.bind(route)
        require(session.route == route) { "bound session changed the canonical route" }
        checks += "binds-canonical-route"

        val first = session.match(matchedSample)
        require(first is MapMatchResult.Matched) { "fixture-defined matched sample did not match" }
        first.requireMatches(route, matchedSample, matcher.descriptor.id)
        checks += "returns-canonical-match"

        if (MapMatchingCapabilities.Deterministic in matcher.descriptor.capabilities) {
            val second = session.match(matchedSample)
            require(first == second) { "deterministic matcher changed result for identical input" }
            checks += "deterministic-repeat"
        }

        val unmatched = session.match(unmatchedSample)
        require(unmatched is MapMatchResult.Unmatched) {
            "fixture-defined unmatched sample must return Unmatched, not a failure"
        }
        checks += "explicit-unmatched"

        return MapMatcherContractReport(
            providerId = matcher.descriptor.id.value,
            checks = checks.toList(),
        )
    }
}
