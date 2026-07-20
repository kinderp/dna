package org.traveldna.navigation.matching.fake

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import org.traveldna.geo.contracts.GeoPoint
import org.traveldna.location.contracts.LocationSequence
import org.traveldna.navigation.contracts.MatchConfidence
import org.traveldna.navigation.contracts.MatchedRoutePosition
import org.traveldna.navigation.contracts.RouteCoordinate
import org.traveldna.navigation.matching.contracts.MapMatchErrorCode
import org.traveldna.navigation.matching.contracts.MapMatchProvenance
import org.traveldna.navigation.matching.contracts.MapMatchResult
import org.traveldna.navigation.matching.testkit.MapMatcherContractProbe
import org.traveldna.routing.contracts.RouteLeg
import org.traveldna.routing.contracts.RouteManeuver
import org.traveldna.routing.contracts.RoutePlan

class FakeMapMatcherTest {
    @Test
    fun passesTheReusableContractProbe() {
        val matcher = FakeMapMatchFixtures.matcher()
        val report = runImmediate {
            MapMatcherContractProbe.verify(
                matcher = matcher,
                route = FakeMapMatchFixtures.Route,
                matchedSample = FakeMapMatchFixtures.MatchedSample,
                unmatchedSample = FakeMapMatchFixtures.UnmatchedSample,
                failureSample = FakeMapMatchFixtures.FailureSample,
                expectedFailureCode = MapMatchErrorCode.ProviderUnavailable,
            )
        }
        assertEquals(FakeMapMatcher.Id.value, report.providerId)
        assertEquals(6, report.checks.size)
    }

    @Test
    fun catalogMissIsAnExplicitUnmatchedOutcome() {
        val matcher = FakeMapMatcher(emptyList())
        val session = matcher.bind(FakeMapMatchFixtures.Route)
        val result = runImmediate { session.match(FakeMapMatchFixtures.MatchedSample) }
        val unmatched = assertIs<MapMatchResult.Unmatched>(result)
        assertEquals("fake.catalog-miss", unmatched.unmatched.providerDiagnosticCode)
    }

    @Test
    fun catalogFailureRemainsDistinctFromUnmatched() {
        val matcher = FakeMapMatchFixtures.matcher()
        val session = matcher.bind(FakeMapMatchFixtures.Route)
        val result = runImmediate { session.match(FakeMapMatchFixtures.FailureSample) }
        val failure = assertIs<MapMatchResult.Failure>(result)
        assertEquals(MapMatchErrorCode.ProviderUnavailable, failure.error.code)
        assertEquals(true, failure.error.retryable)
    }

    @Test
    fun callRecordingIsBoundedAndResettable() {
        val matcher = FakeMapMatchFixtures.matcher(maxRecordedCalls = 2)
        val session = matcher.bind(FakeMapMatchFixtures.Route)
        runImmediate { FakeMapMatchFixtures.Samples.take(3).forEach { session.match(it) } }
        assertEquals(3L, matcher.totalMatchCount)
        assertEquals(FakeMapMatchFixtures.Samples.subList(1, 3), matcher.recordedCalls.map { it.sample })
        matcher.resetRecordedCalls()
        assertEquals(0L, matcher.totalMatchCount)
        assertEquals(emptyList(), matcher.recordedCalls)
    }

    @Test
    fun invalidMatchedCatalogEntryIsRejectedAtConstruction() {
        val sample = FakeMapMatchFixtures.MatchedSample
        val invalid = MapMatchResult.Matched(
            position = MatchedRoutePosition(
                routeId = FakeMapMatchFixtures.Route.id,
                sampleSequence = LocationSequence(99),
                monotonicTime = sample.monotonicTime,
                coordinate = RouteCoordinate(0, 0.0),
                lateralDistanceMeters = 0.0,
                confidence = MatchConfidence.High,
            ),
            provenance = MapMatchProvenance(FakeMapMatcher.Id),
        )
        assertFailsWith<IllegalArgumentException> {
            FakeMapMatcher(listOf(FakeMapMatchEntry(FakeMapMatchFixtures.Route, sample, invalid)))
        }
    }

    @Test
    fun bindRejectsAReusedRouteIdWithDifferentCanonicalGeometry() {
        val matcher = FakeMapMatchFixtures.matcher()
        val original = FakeMapMatchFixtures.Route
        val alteredGeometry = original.geometry.mapIndexed { index, point ->
            if (index == 1) GeoPoint(point.latitude + 0.001, point.longitude) else point
        }
        val alteredLegs = original.legs.map { leg ->
            RouteLeg(
                geometryStartIndex = leg.geometryStartIndex,
                geometryEndIndex = leg.geometryEndIndex,
                origin = alteredGeometry[leg.geometryStartIndex],
                destination = alteredGeometry[leg.geometryEndIndex],
                distanceMeters = leg.distanceMeters,
                durationSeconds = leg.durationSeconds,
                maneuvers = leg.maneuvers.map { maneuver ->
                    RouteManeuver(
                        geometryIndex = maneuver.geometryIndex,
                        type = maneuver.type,
                        location = alteredGeometry[maneuver.geometryIndex],
                        instruction = maneuver.instruction,
                        roadName = maneuver.roadName,
                        exitNumber = maneuver.exitNumber,
                    )
                },
            )
        }
        val altered = RoutePlan(
            id = original.id,
            geometry = alteredGeometry,
            legs = alteredLegs,
            distanceMeters = original.distanceMeters,
            durationSeconds = original.durationSeconds,
            provenance = original.provenance,
        )
        assertFailsWith<IllegalArgumentException> { matcher.bind(altered) }
    }
}

private fun <T> runImmediate(block: suspend () -> T): T {
    var outcome: Result<T>? = null
    block.startCoroutine(object : Continuation<T> {
        override val context = EmptyCoroutineContext
        override fun resumeWith(result: Result<T>) {
            outcome = result
        }
    })
    return checkNotNull(outcome) { "deterministic fake unexpectedly suspended" }.getOrThrow()
}
