package org.traveldna.navigation.matching.testkit

import org.traveldna.navigation.matching.contracts.MapMatchRequest
import org.traveldna.navigation.matching.contracts.MapMatchResult
import org.traveldna.navigation.matching.contracts.MapMatcherPort
import org.traveldna.navigation.matching.contracts.MapMatchingCapabilities
import org.traveldna.navigation.matching.contracts.requireMatches

data class MapMatcherContractReport(
    val providerId: String,
    val checks: List<String>,
)

/** Reusable conformance probe for deterministic route-constrained matchers. */
object MapMatcherContractProbe {
    suspend fun verify(
        matcher: MapMatcherPort,
        matchedRequest: MapMatchRequest,
        unmatchedRequest: MapMatchRequest,
    ): MapMatcherContractReport {
        val checks = mutableListOf<String>()
        require(MapMatchingCapabilities.MatchRoute in matcher.descriptor.capabilities) {
            "map matcher must declare navigation.map-match"
        }
        checks += "declares-map-match"

        val first = matcher.match(matchedRequest)
        require(first is MapMatchResult.Matched) { "fixture-defined matched request did not match" }
        first.requireMatches(matchedRequest, matcher.descriptor.id)
        checks += "returns-canonical-match"

        if (MapMatchingCapabilities.Deterministic in matcher.descriptor.capabilities) {
            val second = matcher.match(matchedRequest)
            require(first == second) { "deterministic matcher changed result for identical request" }
            checks += "deterministic-repeat"
        }

        val unmatched = matcher.match(unmatchedRequest)
        require(unmatched is MapMatchResult.Unmatched) {
            "fixture-defined unmatched request must return Unmatched, not a failure"
        }
        checks += "explicit-unmatched"

        return MapMatcherContractReport(
            providerId = matcher.descriptor.id.value,
            checks = checks.toList(),
        )
    }
}
