package org.traveldna.navigation.matching.contracts

import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.navigation.contracts.MatchConfidence
import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.navigation.contracts.RouteCoordinate
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance

class MapMatchingContractsTest {
    @Test
    fun matchedPostconditionPreservesRequestIdentityAndGeometryBounds() {
        val request = request()
        matched(request).requireMatches(request, ProviderId)

        assertFailsWith<IllegalArgumentException> {
            matched(request, routeId = RouteId("other-route-v0")).requireMatches(request, ProviderId)
        }
        assertFailsWith<IllegalArgumentException> {
            matched(request, sequence = 2L).requireMatches(request, ProviderId)
        }
        assertFailsWith<IllegalArgumentException> {
            matched(request, index = 99).requireMatches(request, ProviderId)
        }
        assertFailsWith<IllegalArgumentException> {
            matched(request, providerId = PluginId("org.traveldna.other-matcher"))
                .requireMatches(request, ProviderId)
        }
    }

    @Test
    fun finalGeometryPointRequiresZeroFraction() {
        val request = request()
        assertFailsWith<IllegalArgumentException> {
            matched(request, index = 1, fraction = 0.5).requireMatches(request, ProviderId)
        }
    }

    @Test
    fun diagnosticsAndInvalidRequestFailureAreBounded() {
        assertFailsWith<IllegalArgumentException> {
            MapMatchUnmatched(MapMatchUnmatchedReason.NoCandidate, "x".repeat(129))
        }
        assertFailsWith<IllegalArgumentException> {
            MapMatchError(MapMatchErrorCode.InvalidRequest, "invalid", retryable = true)
        }
        assertFailsWith<IllegalArgumentException> {
            MapMatchProvenance(ProviderId, "x".repeat(257))
        }
    }

    private fun request(): MapMatchRequest {
        val a = GeoPoint(0.0, 0.0)
        val b = GeoPoint(0.0, 0.01)
        val route = RoutePlan(
            id = RouteId("contract-match-route-v0"),
            geometry = listOf(a, b),
            legs = listOf(RouteLeg(0, 1, a, b, 1_000L, 60L, emptyList())),
            distanceMeters = 1_000L,
            durationSeconds = 60L,
            provenance = RouteProvenance(PluginId("org.traveldna.contract-route-provider")),
        )
        return MapMatchRequest(
            route = route,
            sample = LocationSample(
                sequence = LocationSequence(1),
                monotonicTime = MonotonicInstant(1_000L),
                position = a,
                horizontalAccuracyMeters = 5.0,
                origin = LocationSampleOrigin.Replay,
            ),
        )
    }

    private fun matched(
        request: MapMatchRequest,
        routeId: RouteId = request.route.id,
        sequence: Long = request.sample.sequence.value,
        index: Int = 0,
        fraction: Double = 0.0,
        providerId: PluginId = ProviderId,
    ): MapMatchResult.Matched = MapMatchResult.Matched(
        position = MatchedRoutePosition(
            routeId = routeId,
            sampleSequence = LocationSequence(sequence),
            monotonicTime = request.sample.monotonicTime,
            coordinate = RouteCoordinate(index, fraction),
            lateralDistanceMeters = 1.0,
            confidence = MatchConfidence.High,
        ),
        provenance = MapMatchProvenance(providerId),
    )

    private companion object {
        val ProviderId = PluginId("org.traveldna.contract-map-matcher")
    }
}
