package org.traveldna.navigation.matching.contracts

import org.traveldna.location.contracts.LocationSample
import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.plugin.sdk.CapabilityId
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.plugin.sdk.TravelDnaPlugin
import org.traveldna.routing.contracts.RoutePlan

object MapMatchingCapabilities {
    val MatchRoute = CapabilityId("navigation.map-match")
    val Deterministic = CapabilityId("navigation.map-match.deterministic")
    val Offline = CapabilityId("navigation.map-match.offline")
}

enum class MapMatchUnmatchedReason {
    NoCandidate,
    InsufficientConfidence,
    OutsideRouteEnvelope,
}

data class MapMatchUnmatched(
    val reason: MapMatchUnmatchedReason,
    val providerDiagnosticCode: String? = null,
) {
    init {
        require(
            providerDiagnosticCode == null ||
                (providerDiagnosticCode.isNotBlank() && providerDiagnosticCode.length <= MaxDiagnosticCodeLength),
        ) {
            "unmatched diagnostic code must be null or non-blank and at most $MaxDiagnosticCodeLength characters"
        }
    }

    companion object {
        const val MaxDiagnosticCodeLength: Int = 128
    }
}

enum class MapMatchErrorCode {
    InvalidRequest,
    ProviderUnavailable,
    Timeout,
    RateLimited,
    Internal,
}

data class MapMatchError(
    val code: MapMatchErrorCode,
    val message: String,
    val retryable: Boolean,
    val providerDiagnosticCode: String? = null,
) {
    init {
        require(message.isNotBlank() && message.length <= MaxMessageLength) {
            "map-matching error message must be non-blank and at most $MaxMessageLength characters"
        }
        require(
            providerDiagnosticCode == null ||
                (providerDiagnosticCode.isNotBlank() && providerDiagnosticCode.length <= MaxDiagnosticCodeLength),
        ) {
            "provider diagnostic code must be null or non-blank and at most $MaxDiagnosticCodeLength characters"
        }
        if (code == MapMatchErrorCode.InvalidRequest) {
            require(!retryable) { "InvalidRequest is not retryable without changing the request" }
        }
    }

    companion object {
        const val MaxMessageLength: Int = 512
        const val MaxDiagnosticCodeLength: Int = 128
    }
}

data class MapMatchProvenance(
    val providerId: PluginId,
    val providerMatchId: String? = null,
) {
    init {
        require(
            providerMatchId == null ||
                (providerMatchId.isNotBlank() && providerMatchId.length <= MaxProviderMatchIdLength),
        ) {
            "provider match id must be null or non-blank and at most $MaxProviderMatchIdLength characters"
        }
    }

    companion object {
        const val MaxProviderMatchIdLength: Int = 256
    }
}

sealed interface MapMatchResult {
    data class Matched(
        val position: MatchedRoutePosition,
        val provenance: MapMatchProvenance,
    ) : MapMatchResult

    data class Unmatched(val unmatched: MapMatchUnmatched) : MapMatchResult

    data class Failure(val error: MapMatchError) : MapMatchResult
}

/** Route-bound matching session used by the per-sample hot path. */
interface MapMatchSession {
    val route: RoutePlan

    suspend fun match(sample: LocationSample): MapMatchResult
}

/** Provider-neutral factory that validates/binds a route once per session. */
interface MapMatcherPort : TravelDnaPlugin {
    fun bind(route: RoutePlan): MapMatchSession
}

fun MatchedRoutePosition.requireMatches(
    route: RoutePlan,
    sample: LocationSample,
): MatchedRoutePosition {
    require(routeId == route.id) { "matched route id differs from the bound route" }
    require(sampleSequence == sample.sequence) { "matched sequence differs from the input sample" }
    require(monotonicTime == sample.monotonicTime) { "matched time differs from the input sample" }
    require(coordinate.completedGeometryIndex in route.geometry.indices) {
        "matched geometry index lies outside the bound route"
    }
    require(
        coordinate.completedGeometryIndex != route.geometry.lastIndex ||
            coordinate.fractionToNext == 0.0,
    ) {
        "matched final geometry point cannot contain next-segment progress"
    }
    return this
}

fun MapMatchResult.Matched.requireMatches(
    route: RoutePlan,
    sample: LocationSample,
    expectedProviderId: PluginId,
): MapMatchResult.Matched {
    position.requireMatches(route, sample)
    require(provenance.providerId == expectedProviderId) {
        "map-match provenance provider differs from the selected plugin"
    }
    return this
}
