package org.traveldna.navigation.matching.fake

import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSample
import org.traveldna.location.contracts.LocationSampleOrigin
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.location.contracts.MonotonicInstant
import org.traveldna.navigation.contracts.MatchConfidence
import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.navigation.contracts.RouteCoordinate
import org.traveldna.navigation.matching.contracts.MapMatchError
import org.traveldna.navigation.matching.contracts.MapMatchErrorCode
import org.traveldna.navigation.matching.contracts.MapMatchProvenance
import org.traveldna.navigation.matching.contracts.MapMatchResult
import org.traveldna.navigation.matching.contracts.MapMatchUnmatched
import org.traveldna.navigation.matching.contracts.MapMatchUnmatchedReason
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.ManeuverType
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RouteManeuver
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance

object FakeMapMatchFixtures {
    val Route: RoutePlan = buildRoute()
    val Samples: List<LocationSample> = listOf(
        sample(0, 0, Route.geometry[0]),
        sample(1, 1_000, Route.geometry[1]),
        sample(2, 2_000, GeoPoint(1.0, 1.0)),
        sample(3, 3_000, Route.geometry[2]),
        sample(4, 4_000, Route.geometry[3]),
        sample(5, 5_000, GeoPoint(-1.0, -1.0)),
    )
    val MatchedSample: LocationSample = Samples[0]
    val UnmatchedSample: LocationSample = Samples[2]
    val FailureSample: LocationSample = Samples[5]

    private val entries: List<FakeMapMatchEntry> = listOf(
        matchedEntry(Samples[0], 0),
        matchedEntry(Samples[1], 1),
        FakeMapMatchEntry(
            Route,
            Samples[2],
            MapMatchResult.Unmatched(
                MapMatchUnmatched(MapMatchUnmatchedReason.NoCandidate, "fixture.no-candidate"),
            ),
        ),
        matchedEntry(Samples[3], 2),
        matchedEntry(Samples[4], 3),
        FakeMapMatchEntry(
            Route,
            Samples[5],
            MapMatchResult.Failure(
                MapMatchError(
                    code = MapMatchErrorCode.ProviderUnavailable,
                    message = "fixture provider unavailable",
                    retryable = true,
                    providerDiagnosticCode = "fixture.provider-down",
                ),
            ),
        ),
    )

    fun matcher(maxRecordedCalls: Int = FakeMapMatcher.DefaultMaxRecordedCalls): FakeMapMatcher =
        FakeMapMatcher(entries, maxRecordedCalls = maxRecordedCalls)

    private fun matchedEntry(sample: LocationSample, index: Int): FakeMapMatchEntry =
        FakeMapMatchEntry(
            route = Route,
            sample = sample,
            result = MapMatchResult.Matched(
                position = MatchedRoutePosition(
                    routeId = Route.id,
                    sampleSequence = sample.sequence,
                    monotonicTime = sample.monotonicTime,
                    coordinate = RouteCoordinate(index, 0.0),
                    lateralDistanceMeters = 1.5,
                    confidence = MatchConfidence.High,
                ),
                provenance = MapMatchProvenance(
                    providerId = FakeMapMatcher.Id,
                    providerMatchId = "fixture-match-${sample.sequence.value}",
                ),
            ),
        )

    private fun sample(sequence: Long, time: Long, point: GeoPoint): LocationSample = LocationSample(
        sequence = LocationSequence(sequence),
        monotonicTime = MonotonicInstant(time),
        position = point,
        horizontalAccuracyMeters = 5.0,
        speedMetersPerSecond = 15.0,
        bearingDegrees = 90.0,
        origin = LocationSampleOrigin.Replay,
    )

    private fun buildRoute(): RoutePlan {
        val a = GeoPoint(0.0, 0.0)
        val b = GeoPoint(0.0, 0.01)
        val c = GeoPoint(0.01, 0.01)
        val d = GeoPoint(0.01, 0.02)
        return RoutePlan(
            id = RouteId("reference-map-match-route-v0"),
            geometry = listOf(a, b, c, d),
            legs = listOf(
                RouteLeg(
                    0,
                    2,
                    a,
                    c,
                    2_400L,
                    160L,
                    listOf(
                        RouteManeuver(0, ManeuverType.Depart, a, "Depart"),
                        RouteManeuver(1, ManeuverType.TurnRight, b, "Turn right"),
                        RouteManeuver(2, ManeuverType.KeepRight, c, "Finish first leg"),
                    ),
                ),
                RouteLeg(
                    2,
                    3,
                    c,
                    d,
                    1_200L,
                    80L,
                    listOf(
                        RouteManeuver(2, ManeuverType.Continue, c, "Continue"),
                        RouteManeuver(3, ManeuverType.Arrive, d, "Arrive"),
                    ),
                ),
            ),
            distanceMeters = 3_600L,
            durationSeconds = 240L,
            provenance = RouteProvenance(PluginId("org.traveldna.fixture-route-provider")),
        )
    }
}
