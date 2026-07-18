package org.traveldna.navigation.matching.testkit

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
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
import org.traveldna.navigation.matching.contracts.MapMatchSession
import org.traveldna.navigation.matching.contracts.MapMatchUnmatched
import org.traveldna.navigation.matching.contracts.MapMatchUnmatchedReason
import org.traveldna.navigation.matching.contracts.MapMatcherPort
import org.traveldna.navigation.matching.contracts.MapMatchingCapabilities
import org.traveldna.plugin.sdk.KnownPlatforms
import org.traveldna.plugin.sdk.PluginDescriptor
import org.traveldna.plugin.sdk.PluginId
import org.traveldna.routing.contracts.RouteId
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RoutePlan
import org.traveldna.routing.contracts.RouteProvenance

class MapMatcherContractProbeTest {
    @Test
    fun deterministicRepeatUsesAFreshSession() {
        val fixture = Fixture()
        val report = runImmediate {
            MapMatcherContractProbe.verify(
                matcher = FreshSessionMatcher(fixture),
                route = fixture.route,
                matchedSample = fixture.matched,
                unmatchedSample = fixture.unmatched,
                failureSample = fixture.failure,
                expectedFailureCode = MapMatchErrorCode.ProviderUnavailable,
            )
        }

        assertEquals(6, report.checks.size)
        assertEquals("deterministic-fresh-session-repeat", report.checks[3])
    }

    @Test
    fun reportDefensivelyCopiesChecks() {
        val mutable = mutableListOf("first")
        val report = MapMatcherContractReport("org.traveldna.test-matcher", mutable)
        mutable.clear()
        assertEquals(listOf("first"), report.checks)
    }
}

private class FreshSessionMatcher(
    private val fixture: Fixture,
) : MapMatcherPort {
    override val descriptor: PluginDescriptor = PluginDescriptor(
        id = ProviderId,
        implementationVersion = "0.1.0",
        contractVersion = 1,
        capabilities = setOf(
            MapMatchingCapabilities.MatchRoute,
            MapMatchingCapabilities.Deterministic,
        ),
        supportedPlatforms = setOf(KnownPlatforms.Jvm),
    )

    override fun bind(route: RoutePlan): MapMatchSession {
        require(route == fixture.route)
        return object : MapMatchSession {
            override val route: RoutePlan = route
            private var matchedSampleSeen = false

            override suspend fun match(sample: LocationSample): MapMatchResult = when (sample) {
                fixture.matched -> {
                    check(!matchedSampleSeen) {
                        "the probe repeated the deterministic sample in one stateful session"
                    }
                    matchedSampleSeen = true
                    MapMatchResult.Matched(
                        position = MatchedRoutePosition(
                            routeId = route.id,
                            sampleSequence = sample.sequence,
                            monotonicTime = sample.monotonicTime,
                            coordinate = RouteCoordinate(0, 0.0),
                            lateralDistanceMeters = 1.0,
                            confidence = MatchConfidence.High,
                        ),
                        provenance = MapMatchProvenance(ProviderId),
                    )
                }
                fixture.unmatched -> MapMatchResult.Unmatched(
                    MapMatchUnmatched(MapMatchUnmatchedReason.NoCandidate),
                )
                fixture.failure -> MapMatchResult.Failure(
                    MapMatchError(
                        code = MapMatchErrorCode.ProviderUnavailable,
                        message = "fixture provider unavailable",
                        retryable = true,
                    ),
                )
                else -> error("unexpected sample")
            }
        }
    }
}

private class Fixture {
    private val start = GeoPoint(0.0, 0.0)
    private val end = GeoPoint(0.0, 0.01)

    val route: RoutePlan = RoutePlan(
        id = RouteId("test-map-match-route-v0"),
        geometry = listOf(start, end),
        legs = listOf(RouteLeg(0, 1, start, end, 1_000L, 60L, emptyList())),
        distanceMeters = 1_000L,
        durationSeconds = 60L,
        provenance = RouteProvenance(PluginId("org.traveldna.test-route-provider")),
    )

    val matched: LocationSample = sample(0, start)
    val unmatched: LocationSample = sample(1, GeoPoint(1.0, 1.0))
    val failure: LocationSample = sample(2, end)

    private fun sample(sequence: Long, point: GeoPoint): LocationSample = LocationSample(
        sequence = LocationSequence(sequence),
        monotonicTime = MonotonicInstant(sequence * 1_000L),
        position = point,
        horizontalAccuracyMeters = 5.0,
        origin = LocationSampleOrigin.Replay,
    )
}

private fun <T> runImmediate(block: suspend () -> T): T {
    var outcome: Result<T>? = null
    block.startCoroutine(object : Continuation<T> {
        override val context = EmptyCoroutineContext
        override fun resumeWith(result: Result<T>) {
            outcome = result
        }
    })
    return checkNotNull(outcome) { "test matcher unexpectedly suspended" }.getOrThrow()
}

private val ProviderId = PluginId("org.traveldna.test-map-matcher")
