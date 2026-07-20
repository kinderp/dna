package org.traveldna.navigation.matching.testkit

import org.traveldna.location.contracts.LocationSample
import org.traveldna.navigation.matching.contracts.MapMatchErrorCode
import org.traveldna.navigation.matching.contracts.MapMatchResult
import org.traveldna.navigation.matching.contracts.MapMatcherPort
import org.traveldna.navigation.matching.contracts.MapMatchingCapabilities
import org.traveldna.navigation.matching.contracts.requireMatches
import org.traveldna.routing.contracts.RoutePlan

/** Immutable, bounded report emitted by the reusable conformance probe. */
class MapMatcherContractReport(
    val providerId: String,
    checks: List<String>,
) {
    val checks: List<String> = checks.toList()

    init {
        require(providerId.isNotBlank() && providerId.length <= MaxProviderIdLength) {
            "provider id must be non-blank and at most $MaxProviderIdLength characters"
        }
        require(this.checks.isNotEmpty() && this.checks.size <= MaxChecks) {
            "contract report must contain between 1 and $MaxChecks checks"
        }
        require(this.checks.distinct().size == this.checks.size) {
            "contract report checks must be unique"
        }
        require(this.checks.all { it.isNotBlank() && it.length <= MaxCheckLength }) {
            "contract report checks must be non-blank and at most $MaxCheckLength characters"
        }
    }

    override fun equals(other: Any?): Boolean =
        other is MapMatcherContractReport && providerId == other.providerId && checks == other.checks

    override fun hashCode(): Int = 31 * providerId.hashCode() + checks.hashCode()

    override fun toString(): String =
        "MapMatcherContractReport(providerId=$providerId, checks=$checks)"

    companion object {
        const val MaxProviderIdLength: Int = 128
        const val MaxChecks: Int = 32
        const val MaxCheckLength: Int = 128
    }
}

/** Reusable conformance probe for deterministic route-constrained matchers. */
object MapMatcherContractProbe {
    suspend fun verify(
        matcher: MapMatcherPort,
        route: RoutePlan,
        matchedSample: LocationSample,
        unmatchedSample: LocationSample,
        failureSample: LocationSample,
        expectedFailureCode: MapMatchErrorCode,
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
            // A matching session may legitimately maintain stream history. Determinism
            // therefore compares the same input from a fresh, equivalently bound session
            // instead of requiring duplicate samples to be idempotent in one session.
            val repeatSession = matcher.bind(route)
            val second = repeatSession.match(matchedSample)
            require(first == second) {
                "deterministic matcher changed result for identical input on a fresh session"
            }
            checks += "deterministic-fresh-session-repeat"
        }

        val unmatched = session.match(unmatchedSample)
        require(unmatched is MapMatchResult.Unmatched) {
            "fixture-defined unmatched sample must return Unmatched, not a failure"
        }
        checks += "explicit-unmatched"

        val failure = session.match(failureSample)
        require(failure is MapMatchResult.Failure) {
            "fixture-defined provider failure must remain distinct from Unmatched"
        }
        require(failure.error.code == expectedFailureCode) {
            "provider failure code differs from fixture expectation"
        }
        checks += "explicit-provider-failure"

        return MapMatcherContractReport(
            providerId = matcher.descriptor.id.value,
            checks = checks,
        )
    }
}
