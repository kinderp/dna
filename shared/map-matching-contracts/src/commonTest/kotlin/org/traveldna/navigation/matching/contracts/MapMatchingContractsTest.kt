package org.traveldna.navigation.matching.contracts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
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
    fun matchedPostconditionPreservesRouteSampleIdentityAndGeometryBounds() {
        val route = route()
        val sample = sample(route.geometry.first())
        matched(route, sample).requireMatches(route, sample, ProviderId)

        assertFailsWith<IllegalArgumentException> {
            matched(route, sample, routeId = RouteId("other-route-v0"))
                .requireMatches(route, sample, ProviderId)
        }
        assertFailsWith<IllegalArgumentException> {
            matched(route, sample, sequence = 2L).requireMatches(route, sample, ProviderId)
        }
        assertFailsWith<IllegalArgumentException> {
            matched(route, sample, time = 2_000L).requireMatches(route, sample, ProviderId)
        }
        assertFailsWith<IllegalArgumentException> {
            matched(route, sample, index = 99).requireMatches(route, sample, ProviderId)
        }
        assertFailsWith<IllegalArgumentException> {
            matched(route, sample, providerId = PluginId("org.traveldna.other-matcher"))
                .requireMatches(route, sample, ProviderId)
        }
    }

    @Test
    fun finalGeometryPointRequiresZeroFraction() {
        val route = route()
        val sample = sample(route.geometry.last())
        assertFailsWith<IllegalArgumentException> {
            matched(route, sample, index = 1, fraction = 0.5)
                .requireMatches(route, sample, ProviderId)
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

    @Test
    fun providerFailureRemainsDistinctFromUnmatched() {
        val failure = MapMatchResult.Failure(
            MapMatchError(
                code = MapMatchErrorCode.ProviderUnavailable,
                message = "provider unavailable",
                retryable = true,
            ),
        )
        assertIs<MapMatchResult.Failure>(failure)
        assertEquals(true, failure.error.retryable)
    }

    private fun route(): RoutePlan {
        val a = GeoPoint(0.0, 0.0)
        val b = GeoPoint(0.0, 0.01)
        return RoutePlan(
            id = RouteId("contract-match-route-v0"),
            geometry = listOf(a, b),
            legs = listOf(RouteLeg(0, 1, a, b, 1_000L, 60L, emptyList())),
            distanceMeters = 1_000L,
            durationSeconds = 60L,
            provenance = RouteProvenance(PluginId("org.traveldna.contract-route-provider")),
        )
    }

    private fun sample(point: GeoPoint): LocationSample = LocationSample(
        sequence = LocationSequence(1),
        monotonicTime = MonotonicInstant(1_000L),
        position = point,
        horizontalAccuracyMeters = 5.0,
        origin = LocationSampleOrigin.Replay,
    )

    private fun matched(
        route: RoutePlan,
        sample: LocationSample,
        routeId: RouteId = route.id,
        sequence: Long = sample.sequence.value,
        time: Long = sample.monotonicTime.milliseconds,
        index: Int = 0,
        fraction: Double = 0.0,
        providerId: PluginId = ProviderId,
    ): MapMatchResult.Matched = MapMatchResult.Matched(
        position = MatchedRoutePosition(
            routeId = routeId,
            sampleSequence = LocationSequence(sequence),
            monotonicTime = MonotonicInstant(time),
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
