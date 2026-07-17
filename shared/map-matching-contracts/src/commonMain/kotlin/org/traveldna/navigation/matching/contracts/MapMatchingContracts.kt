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

data class MapMatchRequest(
    val route: RoutePlan,
    val sample: LocationSample,
)

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

/** Provider-neutral route-constrained map-matching boundary. */
interface MapMatcherPort : TravelDnaPlugin {
    suspend fun match(request: MapMatchRequest): MapMatchResult
}

/** Verifies that a matched position is a valid projection of this exact request. */
fun MatchedRoutePosition.requireMatches(request: MapMatchRequest): MatchedRoutePosition {
    require(routeId == request.route.id) { "matched route id differs from request route" }
    require(sampleSequence == request.sample.sequence) { "matched sequence differs from request sample" }
    require(monotonicTime == request.sample.monotonicTime) { "matched time differs from request sample" }
    require(coordinate.completedGeometryIndex in request.route.geometry.indices) {
        "matched geometry index lies outside the request route"
    }
    require(
        coordinate.completedGeometryIndex != request.route.geometry.lastIndex ||
            coordinate.fractionToNext == 0.0,
    ) {
        "matched final geometry point cannot contain next-segment progress"
    }
    return this
}

fun MapMatchResult.Matched.requireMatches(
    request: MapMatchRequest,
    expectedProviderId: PluginId,
): MapMatchResult.Matched {
    position.requireMatches(request)
    require(provenance.providerId == expectedProviderId) {
        "map-match provenance provider differs from the selected plugin"
    }
    return this
}
